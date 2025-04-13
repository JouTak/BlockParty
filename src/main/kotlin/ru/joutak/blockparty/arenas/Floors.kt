package ru.joutak.blockparty.arenas

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import ru.joutak.blockparty.config.Config
import ru.joutak.blockparty.config.ConfigKeys
import kotlin.random.Random

object Floors {
    private val floorsWorld = Bukkit.getWorld("bp_floors") ?: throw NullPointerException("Мир bp_floors не существует!")
    private val floorBlocks = mutableMapOf<Int, Set<Material>>()

    fun setRandomFloorAt(arena: Arena): Int {
        val floorId = Random.nextInt(Config.get(ConfigKeys.NUMBER_OF_FLOORS))
        for (x in 0..<32) {
            for (z in 0..<32) {
                val newLoc = Location(Bukkit.getWorld(arena.worldName), arena.x1 + x, arena.y1, arena.z1 + z)
                val floorLoc = Location(floorsWorld, 0.0 + x, 0.0, ((33 * floorId) + z).toDouble())
                newLoc.block.type = floorLoc.block.type
            }
        }
        return floorId
    }

    fun getRandomBlock(floorId: Int): Material {
        if (floorId !in floorBlocks.keys) {
            val blocks = mutableSetOf<Material>()
            for (x in 0..<32) {
                for (z in 0..<32) {
                    blocks.add(Location(floorsWorld, 0.0 + x, 0.0, ((33 * floorId) + z).toDouble()).block.type)
                }
            }
            floorBlocks[floorId] = blocks
        }
        return floorBlocks[floorId]!!.random()
    }

    fun removeBlocksExcept(
        arena: Arena,
        blockToSave: Material,
    ) {
        for (x in 0..<32) {
            for (z in 0..<32) {
                val location = Location(Bukkit.getWorld(arena.worldName), arena.x1 + x, arena.y1, arena.z1 + z)
                if (location.block.type != blockToSave) {
                    location.block.type = Material.AIR
                }
            }
        }
    }
}
