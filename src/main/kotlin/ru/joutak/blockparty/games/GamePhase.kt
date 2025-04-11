package ru.joutak.blockparty.games

enum class GamePhase {
    ROUND_START,
    CHOOSE_BLOCK,
    COUNTDOWN,
    BREAK_FLOOR,
    CHECK_PLAYERS,
    FINISH,
    ;

    override fun toString(): String =
        when (this) {
            ROUND_START -> "Подготовка раунда"
            CHOOSE_BLOCK -> "Выбор блока"
            COUNTDOWN -> "Найди или упади"
            BREAK_FLOOR -> "Разрушение пола"
            CHECK_PLAYERS -> "Проверка игроков"
            FINISH -> "Конец игры"
        }
}
