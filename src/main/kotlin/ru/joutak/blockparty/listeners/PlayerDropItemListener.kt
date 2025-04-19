package ru.joutak.blockparty.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import ru.joutak.blockparty.games.GameManager

object PlayerDropItemListener : Listener {
    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) {
        val player = event.player

        if (GameManager.isPlaying(player.uniqueId)) {
            event.isCancelled = true
        }
    }
}
