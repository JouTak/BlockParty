package ru.joutak.blockparty.arenas

import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import ru.joutak.blockparty.Config
import ru.joutak.blockparty.utils.PluginManager
import kotlin.random.Random

data class Arena(
    val name: String,
    val worldName : String,
    val x1: Double, val y1: Double, val z1: Double,
    val x2: Double, val y2: Double, val z2: Double
) {
    private var state = ArenaState.READY
    private var currentFloorId: Int = -1
    val center = Location(Bukkit.getWorld(worldName), (x1 + x2) / 2, y1 + 2, (z1 + z2) / 2)
    val corners = listOf(
        Location(Bukkit.getWorld(worldName), x1, y1 + 2, z1),
        Location(Bukkit.getWorld(worldName), x1, y1 + 2, z2),
        Location(Bukkit.getWorld(worldName), x2, y1 + 2, z2),
        Location(Bukkit.getWorld(worldName), x2, y1 + 2, z1),
    )
    private val threshold = 2

    init {
        this.reset()
    }

    companion object {
        fun deserialize(values: Map<String, Any>): Arena {
            PluginManager.getLogger().info("Десериализация информации об арене ${values["name"]}")
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

    fun launchFireworksAtCorners(amount: Int = 1) {
        val world = Bukkit.getWorld(worldName)!!
        for (corner in corners) {
            for (i in 0..amount) {
                val firework = world.spawnEntity(corner, EntityType.FIREWORK) as Firework
                val meta = firework.fireworkMeta

                meta.addEffect(
                    FireworkEffect.builder()
                        .withColor(Color.fromRGB(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)))
                        .withFade(Color.WHITE)
                        .with(FireworkEffect.Type.BURST)
//                        .trail(true)
//                        .flicker(true)
                        .build()
                )

                meta.power = 1
                firework.fireworkMeta = meta
            }
        }
    }

    fun isInside(playerLoc: Location): Boolean {
        return playerLoc.x in this.x1 - threshold..this.x2 + threshold&&
                playerLoc.y in this.y1 - threshold..this.y2 + threshold &&
                playerLoc.z in this.z1 - threshold..this.z2 + threshold
    }

    fun reset() {
        val world = Bukkit.getWorld(worldName)!!
        val mvWorld = PluginManager.multiverseCore.mvWorldManager.getMVWorld(worldName)

        mvWorld.setTime("day")
        mvWorld.setEnableWeather(false)
        mvWorld.setDifficulty(Difficulty.PEACEFUL)
        mvWorld.setGameMode(GameMode.ADVENTURE)
        mvWorld.setPVPMode(false)
        mvWorld.hunger = false

        world.setGameRule(GameRule.FALL_DAMAGE, false)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false)

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