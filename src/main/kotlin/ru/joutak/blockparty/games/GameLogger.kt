package ru.joutak.blockparty.games

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import ru.joutak.blockparty.config.Config
import ru.joutak.blockparty.config.ConfigKeys
import ru.joutak.blockparty.utils.PluginManager
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.UUID
import java.util.logging.FileHandler
import java.util.logging.LogRecord
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

class GameLogger(
    val game: Game,
) {
    private val dataFolder: File by lazy {
        val root =
            if (Config.get(ConfigKeys.SPARTAKIADA_MODE)) {
                SpartakiadaManager.spartakiadaFolder
            } else {
                PluginManager.blockParty.dataFolder
            }

        File(root, "games").apply { mkdirs() }
    }

    private val logger = Logger.getLogger("GAME/${game.uuid}")
    private val gameFolder = File(dataFolder, game.uuid.toString())
    private val resultFile = File(gameFolder, "${game.uuid}.yml")
    private val logFile = File(gameFolder, "${game.uuid}.log")
    private val logFileHandler: FileHandler

    init {
        gameFolder.mkdirs()
        resultFile.createNewFile()
        logFile.createNewFile()
        logFileHandler = FileHandler(logFile.absolutePath, true)

        logFileHandler.formatter =
            object : SimpleFormatter() {
                override fun format(record: LogRecord): String {
                    val timestamp = SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(record.millis)
                    return "[$timestamp ${record.level}] [${PluginManager.blockParty.pluginMeta.name}] [GAME/${game.uuid}] ${record.message}\n"
                }
            }

        logger.setParent(PluginManager.getLogger())
        logger.addHandler(logFileHandler)
    }

    fun info(msg: String) {
        logger.useParentHandlers = Config.get(ConfigKeys.LOG_INFO_TO_CONSOLE)
        logger.info(msg)
    }

    fun warning(msg: String) {
        logger.useParentHandlers = true
        logger.warning(msg)
    }

    fun severe(msg: String) {
        logger.useParentHandlers = true
        logger.severe(msg)
    }

    fun saveGameResults() {
        val gameData = YamlConfiguration()

        for ((path, value) in game.serialize()) {
            gameData.set(path, value)
        }

        try {
            gameData.save(resultFile)
        } catch (e: IOException) {
            PluginManager.getLogger().severe("Ошибка при сохранении информации об игре: ${e.message}")
        }
    }

    fun addWinners(winners: Iterable<UUID>) {
        if (!Config.get(ConfigKeys.SPARTAKIADA_MODE)) return
        if (winners.count() == 0) return

        winners.map { Bukkit.getOfflinePlayer(it) }.forEach { SpartakiadaManager.markWinner(it) }
    }

    fun close() {
        logFileHandler.flush()
        logFileHandler.close()
        logger.removeHandler(logFileHandler)
    }
}
