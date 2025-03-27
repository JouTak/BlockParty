package ru.joutak.blockparty.arenas

import org.bukkit.Bukkit
import org.bukkit.Location
import ru.joutak.blockparty.Config
import kotlin.random.Random

class Floor {
    companion object {
        val floorsWorld = Bukkit.getWorld("floors") ?: throw NullPointerException("Мир floors не существует!")

        fun setRandomFloorAt(loc: Location) {
            val floorId = Random.nextInt(Config.NUMBER_OF_FLOORS)
//        val floorLoc = Location(floorsWorld, 0)
            for (x in 0..<32) {
                for (z in 0..<32) {
                    val newLoc = Location(loc.world, loc.x + x, loc.y, loc.z + z)
                    val floorLoc = Location(floorsWorld, 0.0 + x, 0.0, ((33 * floorId) + z).toDouble())
                    newLoc.block.type = floorLoc.block.type
                }
            }
        }
    }
}