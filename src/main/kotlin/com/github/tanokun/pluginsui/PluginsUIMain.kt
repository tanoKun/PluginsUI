package com.github.tanokun.pluginsui

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.github.tanokun.pluginsui.command.FileCommand
import dev.jorel.commandapi.CommandAPI
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

lateinit var pluginsUIMain: PluginsUIMain

class PluginsUIMain : JavaPlugin() {
    lateinit var bukkitAudiences: BukkitAudiences
    lateinit var protocolManager: ProtocolManager
    override fun onEnable() {
        pluginsUIMain = this

        protocolManager = ProtocolLibrary.getProtocolManager()
        bukkitAudiences = BukkitAudiences.create(pluginsUIMain)

        CommandAPI.onEnable(this)
        FileCommand()

    }

    override fun onDisable() {
    }
}