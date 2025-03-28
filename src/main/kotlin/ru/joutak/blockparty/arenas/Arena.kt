package ru.joutak.blockparty.arenas

import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.GameMode
import org.bukkit.Location
import ru.joutak.blockparty.Config
import ru.joutak.blockparty.utils.PluginManager

data class Arena(
    val name: String,
    val worldName : String,
    val x1: Double, val y1: Double, val z1: Double,
    val x2: Double, val y2: Double, val z2: Double
) {
    private var state = ArenaState.READY
    private var currentFloorId: Int = -1
    val center = Location(Bukkit.getWorld(worldName), (x1 + x2) / 2, y1 + 2, (z1 + z2) / 2)
    private val threshold = 2

    init {
        this.reset()
    }

    companion object {
        fun deserialize(values: Map<String, Any>): Arena {
            Bukkit.getLogger().info("Deserializing arena ${values["name"]}")
            return Arena(
                values["name"] as String,
                values["worldName"] as String,
                values["x1"] as Double,
                values["y1"] as Double,
                values["z1"] as Double,
                values["x2"] as Double,
                values["y2"] as Double,
                values["z2"] as Double
            )
        }
    }

    fun getState(): ArenaState {
        return state
    }

    fun getCurrentFloorId(): Int {
        return currentFloorId
    }

    fun setState(state: ArenaState) {
        this.state = state
    }

    fun setCurrentFloorId(floorId: Int) {
        if (floorId !in 0..<Config.NUMBER_OF_FLOORS)
            throw IllegalArgumentException("Неверный floorId: $floorId (макс. допустимое значение ${Config.NUMBER_OF_FLOORS - 1})")

        this.currentFloorId = floorId
    }

    fun isInside(playerLoc: Location): Boolean {
        return playerLoc.x in this.x1 - threshold..this.x2 + threshold&&
                playerLoc.y in this.y1 - threshold..this.y2 + threshold &&
                playerLoc.z in this.z1 - threshold..this.z2 + threshold
    }

    fun reset() {
        val worldManager = PluginManager.multiverseCore.mvWorldManager
        val mvWorld = worldManager.getMVWorld(worldName)
        mvWorld.setTime("day")
        mvWorld.setEnableWeather(false)
        mvWorld.setDifficulty(Difficulty.PEACEFUL)
        mvWorld.setGameMode(GameMode.ADVENTURE)
        mvWorld.setPVPMode(false)
        mvWorld.setHunger(false)
        setState(ArenaState.READY)
    }

    fun serialize(): Map<String, Any> {
        return mapOf(
            "name" to this.name,
            "worldName" to this.worldName,
            "x1" to this.x1,
            "y1" to this.y1,
            "z1" to this.z1,
            "x2" to this.x2,
            "y2" to this.y2,
            "z2" to this.z2
        )
    }
}