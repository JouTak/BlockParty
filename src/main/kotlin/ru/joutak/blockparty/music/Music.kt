package ru.joutak.blockparty.music

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.SoundStop
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.SerializableAs

@SerializableAs("Music")
data class Music(
    val key: String,
    val duration: Int,
    val author: String,
    val title: String,
) : ConfigurationSerializable {
    companion object {
        val defaultComparator =
            compareBy<Music> { music ->
                "${music.author} - ${music.title}"
            }

        @JvmStatic
        fun deserialize(serialized: Map<String, Any>): Music {
            val key = serialized["key"] as String
            val duration = serialized["duration"] as Int
            val author = serialized["author"] as String
            val title = serialized["title"] as String
            return Music(key, duration, author, title)
        }
    }

    val asAuthorTitleString = "$author - $title"

    fun toSound(volume: Float): Sound =
        Sound.sound(
            Key.key(Key.MINECRAFT_NAMESPACE, key),
            Sound.Source.RECORD,
            volume,
            1f,
        )

    fun toSoundStop(): SoundStop = SoundStop.namedOnSource(Key.key(Key.MINECRAFT_NAMESPACE, key), Sound.Source.RECORD)

    override fun serialize(): Map<String, Any> {
        val serializedMusic = mutableMapOf<String, Any>()
        serializedMusic["key"] = key
        serializedMusic["duration"] = duration
        serializedMusic["author"] = author
        serializedMusic["title"] = title
        return serializedMusic
    }
}
