package ru.joutak.blockparty.arenas

import org.bukkit.entity.Player

data class Arena(
    val name: String,
    val worldName : String,
    val x1: Double, val y1: Double, val z1: Double,
    val x2: Double, val y2: Double, val z2: Double
) {
    private var state = ArenaState.READY
    private val players = mutableSetOf<Player>()

    companion object {
        fun deserialize(values: Map<String, Any>): Arena {
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

    fun setState(state: ArenaState) {
        this.state = state
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