package ru.joutak.blockparty.music

class MusicScheduler {
    private val playedMusic: MutableSet<Music> = hashSetOf()

    fun getNextMusic(): Music {
        if (playedMusic.size == MusicManager.getPlaylist().size) {
            playedMusic.clear()
        }

        try {
            val nextMusic =
                MusicManager
                    .getPlaylist()
                    .subtract(playedMusic)
                    .random()
            playedMusic.add(nextMusic)
            return nextMusic
        } catch (e: NoSuchElementException) {
            return MusicManager.getPlaylist().random()
        }
    }
}
