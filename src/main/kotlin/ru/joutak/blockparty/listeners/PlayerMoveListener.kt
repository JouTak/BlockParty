package ru.joutak.blockparty.listeners

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

        if (!playerData.currentArena!!.isInside(location)) {
            player.gameMode = GameMode.SPECTATOR
            val game = GameManager.get(playerData.games.last())
            if (game?.getPhase() != GamePhase.FINISH)
                game?.checkPlayers()
        }

    }
}