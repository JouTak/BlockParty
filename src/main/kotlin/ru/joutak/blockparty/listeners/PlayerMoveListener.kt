package ru.joutak.blockparty.listeners

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.LinearComponents
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import ru.joutak.blockparty.games.GameManager
import ru.joutak.blockparty.games.GamePhase
import ru.joutak.blockparty.players.PlayerData

object PlayerMoveListener : Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val location = player.location
        val playerData = PlayerData.get(player.uniqueId)

        if (!playerData.isInGame())
            return

        val game = GameManager.get(playerData.games.last())

        if (!playerData.currentArena!!.isInside(location) && player.gameMode != GameMode.SPECTATOR) {
            Audience.audience(player).showTitle(
                Title.title(
                    LinearComponents.linear(
                        Component.text("Вы проиграли! :(", NamedTextColor.RED)
                    ),
                    LinearComponents.linear()
                )
            )

            player.gameMode = GameMode.SPECTATOR
            player.teleport(playerData.currentArena!!.center)

            if (game?.getPhase() != GamePhase.FINISH)
                game?.checkPlayers()
        }

    }
}