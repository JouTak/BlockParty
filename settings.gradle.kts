val name: String by settings

rootProject.name = name

pluginManagement {
    val kotlinVersion: String by settings
    val shadowVersion: String by settings

    plugins {
        id("org.jetbrains.kotlin.jvm").version(kotlinVersion)
        id("com.gradleup.shadow").version(shadowVersion)
    }
}
