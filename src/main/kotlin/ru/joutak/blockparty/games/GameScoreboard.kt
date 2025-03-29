package ru.joutak.blockparty.games

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.LinearComponents
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.ScoreboardManager
import java.util.*

class GameScoreboard {
    private val manager: ScoreboardManager = Bukkit.getScoreboardManager()
    private val scoreboard: Scoreboard = manager.newScoreboard
    private var bossBar: BossBar? = null
    private val objective = scoreboard.registerNewObjective("game", Criteria.DUMMY, LinearComponents.linear(
        Component.text("B", NamedTextColor.RED),
        Component.text("l", NamedTextColor.GOLD),
        Component.text("o", NamedTextColor.YELLOW),
        Component.text("c", NamedTextColor.GREEN),
        Component.text("k", NamedTextColor.AQUA),
        Component.text("P", NamedTextColor.BLUE),
        Component.text("a", NamedTextColor.DARK_PURPLE),
        Component.text("r", NamedTextColor.LIGHT_PURPLE),
        Component.text("t", NamedTextColor.WHITE),
        Component.text("y", NamedTextColor.GRAY)
    ))

    init {
        objective.displaySlot = DisplaySlot.SIDEBAR
    }

    fun update(playersLeft: Int, round: Int) {
        scoreboard.entries.forEach { scoreboard.resetScores(it) } // Очищаем старые данные

        objective.getScore("Раунд:").score = round
        objective.getScore("Оставшиеся игроки:").score = playersLeft
    }

    fun setBossBarTimer(playersUuids: List<UUID>, phase: GamePhase, timeLeft: Int, totalTime: Int) {
        val newBossBar: BossBar? = when (phase) {
            GamePhase.ROUND_START,
            GamePhase.BREAK_FLOOR,
            GamePhase.CHECK_PLAYERS -> null

            GamePhase.CHOOSE_BLOCK,
            GamePhase.COUNTDOWN,
            GamePhase.FINISH -> BossBar.bossBar(
                LinearComponents.linear(
                    Component.text(phase.toString()),
                    Component.text(": $timeLeft сек.")
                ),
                timeLeft.toFloat() / totalTime.toFloat(),
                BossBar.Color.WHITE,
                BossBar.Overlay.PROGRESS
            )
        }

        for (playerUuid in playersUuids) {
            val player = Bukkit.getPlayer(playerUuid) ?: continue
            if (bossBar != null)
                player.hideBossBar(bossBar!!)

            if (newBossBar != null)
                player.showBossBar(newBossBar)
        }
        bossBar = newBossBar
    }

    fun setFor(player: Player) {
        player.scoreboard = scoreboard
    }

    fun removeFor(player: Player) {
        player.scoreboard = manager.newScoreboard
        player.hideBossBar(bossBar ?: return)
    }
}
