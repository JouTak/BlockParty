package ru.joutak.blockparty.music

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.LinearComponents
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import ru.joutak.blockparty.utils.PluginManager
import java.util.UUID

class MusicPlayer {
    private val musicScheduler = MusicScheduler()
    private var isMusicPlaying = false
    private var music = musicScheduler.getNextMusic()

    fun playCurrentSong(playersUuids: Iterable<UUID>) {
        if (isMusicPlaying) {
            playFor(playersUuids)
        }
    }

    fun playNextSong(playersUuids: Iterable<UUID>) {
        if (isMusicPlaying) return
        music = musicScheduler.getNextMusic()
        playFor(playersUuids)
        isMusicPlaying = true
    }

    fun stopSong(playersUuids: Iterable<UUID>) {
        if (!isMusicPlaying) return
        stopFor(playersUuids)
        isMusicPlaying = false
    }

    private fun playFor(playersUuids: Iterable<UUID>) {
        for (uuid in playersUuids) {
            Bukkit.getPlayer(uuid)?.let {
                it.playSound(music.toSound(0.25f), it.location.x, it.location.y, it.location.z)
                showSongTitleTo(it, music)
            }
        }
    }

    fun stopFor(playersUuids: Iterable<UUID>) {
        for (uuid in playersUuids) {
            Bukkit.getPlayer(uuid)?.stopSound(music.toSoundStop())
        }
    }

    private fun showSongTitleTo(
        audience: Audience,
        music: Music,
    ) {
        val task =
            Bukkit.getScheduler().runTaskTimer(
                PluginManager.blockParty,
                Runnable {
                    val color = NamedTextColor.NAMES.values().random()

                    audience.sendActionBar(
                        LinearComponents.linear(
                            Component.text("Сейчас играет: ", color, TextDecoration.BOLD),
                            Component.text(music.asAuthorTitleString, color),
                        ),
                    )
                },
                0L,
                5L,
            )

        Bukkit.getScheduler().runTaskLater(
            PluginManager.blockParty,
            Runnable {
                Bukkit.getScheduler().cancelTask(task.taskId)
            },
            60L,
        )
    }
}
