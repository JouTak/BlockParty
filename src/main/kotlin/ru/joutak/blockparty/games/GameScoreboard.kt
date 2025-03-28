package ru.joutak.blockparty.games

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

    fun setXpBarTimer(playersUuids: List<UUID>, timeLeft: Int, totalTime: Int) {
        for (playerUuid in playersUuids) {
            val player = Bukkit.getPlayer(playerUuid) ?: continue
            setXpBarTimer(player, timeLeft, totalTime)
        }
    }

    fun setXpBarTimer(player: Player, timeLeft: Int, totalTime: Int) {
        if (timeLeft <= 0 || !player.isOnline) {
            player.exp = 0.0f
            player.level = 0
            return
        }

        player.exp = timeLeft.toFloat() / totalTime.toFloat()
        player.level = timeLeft
    }

    fun setFor(player: Player) {
        player.scoreboard = scoreboard
    }

    fun removeFor(player: Player) {
        player.scoreboard = manager.newScoreboard
    }
}
