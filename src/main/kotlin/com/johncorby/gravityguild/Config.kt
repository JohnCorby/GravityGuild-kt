package com.johncorby.gravityguild

import hazae41.minecraft.kutils.bukkit.PluginConfigFile
import hazae41.minecraft.kutils.bukkit.init

object Config : PluginConfigFile("config") {
    init {
        PLUGIN.init(this)
    }
}
