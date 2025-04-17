package ru.joutak.blockparty.listeners

import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent

object ProjectileHitEventListener : Listener {
    @EventHandler
    fun onSnowballHit(event: ProjectileHitEvent) {
        val snowball = event.entity
        val hitEntity = event.hitEntity ?: return

        if (snowball is Snowball && hitEntity is Player) {
            val knockback =
                hitEntity.location
                    .toVector()
                    .subtract(snowball.location.toVector())
                    .normalize()
                    .multiply(1.2)
            hitEntity.velocity = knockback.setY(0.3)
        }
    }
}
