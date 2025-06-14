package ru.joutak.blockparty.listeners

import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import ru.joutak.blockparty.games.GameManager
import ru.joutak.blockparty.games.GamePhase

object PlayerMoveListener : Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val location = player.location

        if (!GameManager.isPlaying(player.uniqueId)) {
            return
        }

        val game = GameManager.getByPlayer(player)!!
        val arena = game.arena

        if (!arena.isInside(location) && player.gameMode != GameMode.SPECTATOR) {
            if (game.getPhase() == GamePhase.FINISH) {
                player.teleport(arena.center)
            } else {
                game.knockout(player.uniqueId)
            }
        }

        if (player.location.y < -64) player.teleport(arena.center)
    }
}
