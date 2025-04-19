package ru.joutak.blockparty.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import ru.joutak.blockparty.config.Config
import ru.joutak.blockparty.config.ConfigKeys
import ru.joutak.blockparty.games.SpartakiadaManager

object PlayerLoginListener : Listener {
    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
        if (!Config.get(ConfigKeys.SPARTAKIADA_MODE)) {
            return
        }

        val player = event.player

        if (SpartakiadaManager.canBypass(player)) return

        if (SpartakiadaManager.isWinner(player)) {
            event.disallow(
                PlayerLoginEvent.Result.KICK_OTHER,
                SpartakiadaManager.KICK_WINNER_MESSAGE,
            )
        } else if (!SpartakiadaManager.hasAttempts(player)) {
            event.disallow(
                PlayerLoginEvent.Result.KICK_WHITELIST,
                SpartakiadaManager.KICK_NO_ATTEMPTS_MESSAGE,
            )
        } else if (!SpartakiadaManager.isParticipant(player)) {
            event.disallow(
                PlayerLoginEvent.Result.KICK_WHITELIST,
                SpartakiadaManager.KICK_NON_PARTICIPANT_MESSAGE,
            )
        }
    }
}
