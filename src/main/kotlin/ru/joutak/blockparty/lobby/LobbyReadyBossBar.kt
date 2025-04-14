package ru.joutak.blockparty.lobby

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import ru.joutak.blockparty.players.PlayerData
import ru.joutak.blockparty.players.PlayerState
import java.util.UUID

object LobbyReadyBossBar {
    private val bars = mutableMapOf<UUID, BossBar>()

    fun removeAllBossBars() {
        LobbyManager.getPlayers().forEach { it.activeBossBars().forEach { bar -> it.hideBossBar(bar) } }
    }

    fun setFor(player: Player) {
        val uuid = player.uniqueId
        val state = PlayerData.get(uuid).state

        val text: String
        val color: BossBar.Color
        val progress: Float

        when (state) {
            PlayerState.READY -> {
                text = "Готов к игре!"
                color = BossBar.Color.GREEN
                progress = 1.0f
            }

            PlayerState.LOBBY -> {
                text = "Не готов :("
                color = BossBar.Color.RED
                progress = 1.0f
            }

            else -> {
                text = ""
                color = BossBar.Color.WHITE
                progress = 0.0f
            }
        }

        val bar =
            BossBar.bossBar(
                Component.text(text),
                progress,
                color,
                BossBar.Overlay.PROGRESS,
            )

        removeFor(player)
        player.showBossBar(bar)
        bars[uuid] = bar
    }

    fun removeFor(player: Player) {
        if (bars.containsKey(player.uniqueId)) {
            player.hideBossBar(bars[player.uniqueId]!!)
            bars.remove(player.uniqueId)
        }
    }
}
