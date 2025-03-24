package ru.joutak.blockparty.arenas

object ArenaManager {
    private val arenas = mutableMapOf<String, Arena>()

    fun add(arena: Arena) {
        if (arenas.containsKey(arena.name))
            throw IllegalArgumentException("Арена с таким именем уже существует.")

        arenas[arena.name] = arena
    }

    fun get(name: String): Arena {
        if (!arenas.containsKey(name))
            throw IllegalArgumentException("Арены с таким именем не существует.")

        return arenas[name]!!
    }

    fun getArenas() : Map<String, Arena> {
        return arenas
    }

    fun remove(name: String) {
        if (!arenas.containsKey(name))
            throw IllegalArgumentException("Арены с таким именем не существует.")

        arenas.remove(name)
    }

    fun clear() {
        arenas.clear()
    }
}