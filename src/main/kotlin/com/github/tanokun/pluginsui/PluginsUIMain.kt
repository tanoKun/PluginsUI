package com.github.tanokun.pluginsui

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.github.tanokun.pluginsui.command.File2Command
import com.github.tanokun.pluginsui.command.FileCommand
import com.github.tanokun.pluginsui.ui.ui.anvil.CreateAnvilListener
import com.github.tanokun.pluginsui.ui.ui.anvil.RenameAnvilListener
import com.github.tanokun.pluginsui.ui.ui.anvil.SelectAnvilListener
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.plugin.java.JavaPlugin

lateinit var pluginsUIMain: PluginsUIMain

class PluginsUIMain : JavaPlugin() {
    lateinit var bukkitAudiences: BukkitAudiences
    lateinit var protocolManager: ProtocolManager
    override fun onEnable() {
        pluginsUIMain = this

        protocolManager = ProtocolLibrary.getProtocolManager()
        bukkitAudiences = BukkitAudiences.create(pluginsUIMain)

        CreateAnvilListener()
        RenameAnvilListener()
        SelectAnvilListener()

        CommandAPI.onLoad(CommandAPIBukkitConfig(this))
        CommandAPI.onEnable()
        FileCommand()
        File2Command()
    }

    override fun onDisable() {
    }
}