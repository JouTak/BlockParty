package ru.joutak.blockparty.config

object ConfigKeys {
    val LOBBY_WORLD_NAME = string("LOBBY_WORLD_NAME", "bp_lobby")
    val LOG_INFO_TO_CONSOLE = boolean("LOG_INFO_TO_CONSOLE", false)
    val MAX_PLAYERS_IN_GAME = int("MAX_PLAYERS_IN_GAME", 12)
    val MAX_ROUND_TIME = int("MAX_ROUND_TIME", 10)
    val MIN_ROUND_TIME = int("MIN_ROUND_TIME", 1)
    val NUMBER_OF_FLOORS = int("NUMBER_OF_FLOORS", 1)
    val NUMBER_OF_SNOWBALLS_ON_PVP = int("NUMBER_OF_SNOWBALLS_ON_PVP", 4)
    val PLAYERS_TO_START = int("PLAYERS_TO_START", 8)
    val PLAYERS_TO_END = int("PLAYERS_TO_END", 1)
    val ROUND_TO_START_PVP = int("ROUND_TO_START_PVP", 20)
    val TIME_BETWEEN_ROUNDS = int("TIME_BETWEEN_ROUNDS", 10)
    val TIME_TO_START_GAME_LOBBY = int("TIME_TO_START_GAME_LOBBY", 15)

    val all =
        setOf(
            LOBBY_WORLD_NAME,
            LOG_INFO_TO_CONSOLE,
            MAX_PLAYERS_IN_GAME,
            MAX_ROUND_TIME,
            MIN_ROUND_TIME,
            NUMBER_OF_FLOORS,
            NUMBER_OF_SNOWBALLS_ON_PVP,
            PLAYERS_TO_START,
            PLAYERS_TO_END,
            ROUND_TO_START_PVP,
            TIME_BETWEEN_ROUNDS,
            TIME_TO_START_GAME_LOBBY,
        )

    private fun int(
        path: String,
        default: Int,
    ) = object : ConfigKey<Int> {
        override val path = path
        override val value = default

        override fun parse(input: String) = input.toIntOrNull()
    }

    private fun boolean(
        path: String,
        default: Boolean,
    ) = object : ConfigKey<Boolean> {
        override val path = path
        override val value = default

        override fun parse(input: String): Boolean? =
            when (input.lowercase()) {
                "true", "yes", "1" -> true
                "false", "no", "0" -> false
                else -> null
            }
    }

    private fun string(
        path: String,
        default: String,
    ) = object : ConfigKey<String> {
        override val path = path
        override val value = default

        override fun parse(input: String) = input
    }
}
