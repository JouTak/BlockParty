package ru.joutak.blockparty.games

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.LinearComponents
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.ScoreboardManager
import ru.joutak.blockparty.BlockPartyPlugin
import java.util.UUID

class GameScoreboard {
    private val manager: ScoreboardManager = Bukkit.getScoreboardManager()
    private val scoreboard: Scoreboard = manager.newScoreboard
    private val objective = scoreboard.registerNewObjective("game", Criteria.DUMMY, BlockPartyPlugin.TITLE)

    init {
        objective.displaySlot = DisplaySlot.SIDEBAR
    }

    fun update(
        playersLeft: Int,
        round: Int,
    ) {
        scoreboard.entries.forEach { scoreboard.resetScores(it) }

        objective.getScore("Раунд:").score = round
        objective.getScore("Оставшиеся игроки:").score = playersLeft
    }

    fun setBossBarTimer(
        playersUuids: Iterable<UUID>,
        phase: GamePhase,
        timeLeft: Int,
        totalTime: Int,
    ) {
        val bossBar =
            when (phase) {
                GamePhase.ROUND_START,
                GamePhase.BREAK_FLOOR,
                GamePhase.CHECK_PLAYERS,
                -> null

                GamePhase.CHOOSE_BLOCK,
                GamePhase.COUNTDOWN,
                GamePhase.FINISH,
                ->
                    BossBar.bossBar(
                        LinearComponents.linear(
                            Component.text(phase.toString()),
                            Component.text(": $timeLeft сек."),
                        ),
                        timeLeft.toFloat() / totalTime.toFloat(),
                        BossBar.Color.WHITE,
                        BossBar.Overlay.PROGRESS,
                    )
            } ?: return

        for (playerUuid in playersUuids) {
            val player = Bukkit.getPlayer(playerUuid) ?: continue
            player.activeBossBars().toList().forEach { player.hideBossBar(it) }
            player.showBossBar(bossBar)
        }
    }

    fun setFor(player: Player) {
        player.scoreboard = scoreboard
    }

    fun removeFor(player: Player) {
        player.scoreboard = manager.newScoreboard
        player.activeBossBars().toList().forEach { player.hideBossBar(it) }
    }
}
