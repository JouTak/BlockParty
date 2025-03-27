package ru.joutak.blockparty.games

import org.bukkit.Bukkit
import org.bukkit.Location
import ru.joutak.blockparty.arenas.Arena
import ru.joutak.blockparty.arenas.ArenaState
import ru.joutak.blockparty.arenas.Floor
import ru.joutak.blockparty.utils.PluginManager
import ru.joutak.blockparty.players.PlayerData
import ru.joutak.blockparty.players.PlayerState
import java.util.UUID

class Game (val arena: Arena, val players: List<UUID>) : Runnable {
    val uuid = UUID.randomUUID()
    val winners = mutableListOf<UUID>()
    private var round = 1
    private var phase = GamePhase.WAIT

    private var gameTaskId : Int = -1

    fun start() {
        arena.setState(ArenaState.INGAME)
        phase = GamePhase.WAIT
        round = 1

        val world = Bukkit.getWorld(arena.worldName)
        val location = Location(world, (arena.x1 + arena.x2) / 2, arena.y1 + 2, (arena.z1 + arena.z2) / 2)

        for (player in players) {
            PluginManager.multiverseCore.teleportPlayer(Bukkit.getConsoleSender(), Bukkit.getPlayer(player), location)

            val playerData = PlayerData.getPlayerData(player)
            playerData.games.add(this.uuid)
            playerData.currentArena = this.arena
            playerData.playerState = PlayerState.INGAME
        }

        Bukkit.getScheduler().runTask(
            PluginManager.blockParty,
            this
        )
    }

    override fun run() {
        if (this.phase == GamePhase.WAIT) {
            Floor.setRandomFloorAt(Location(Bukkit.getWorld(arena.worldName), arena.x1, arena.y1, arena.z1))
        }
    }
}
