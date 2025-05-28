package ru.joutak.blockparty.utils

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

object TextColors {
    private val allowedColors =
        listOf(
            NamedTextColor.RED,
            NamedTextColor.GREEN,
            NamedTextColor.BLUE,
            NamedTextColor.YELLOW,
            NamedTextColor.GOLD,
            NamedTextColor.AQUA,
            NamedTextColor.LIGHT_PURPLE,
            NamedTextColor.DARK_PURPLE,
            NamedTextColor.DARK_GREEN,
            NamedTextColor.DARK_AQUA,
            NamedTextColor.DARK_RED,
            NamedTextColor.WHITE,
        )

    fun getRandom(): TextColor = allowedColors.random()
}
