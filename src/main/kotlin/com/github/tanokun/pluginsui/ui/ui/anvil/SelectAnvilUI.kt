package com.github.tanokun.pluginsui.ui.ui.anvil

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.github.tanokun.pluginsui.pluginsUIMain
import com.github.tanokun.pluginsui.ui.AbstractUI
import com.github.tanokun.pluginsui.ui.ui.PluginsUI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.SimpleItem
import xyz.xenondevs.invui.window.AnvilWindow
import java.io.File

private const val PLUGINS_UI_SELECT = "PluginsUI_Select"

class SelectAnvilUI(val file: File): AbstractUI() {
    init {
        guiContext = Gui.normal()
            .setStructure("x # x")
            .addIngredient('x', ItemBuilder(Material.PAPER).setDisplayName("検索したい名前を入力"))
            .build()
    }

    override fun showUI(player: Player) {
        AnvilWindow.single()
            .setGui(guiContext)
            .setTitle("§l検索")
            .addRenameHandler {
                player.setMetadata(PLUGINS_UI_SELECT, FixedMetadataValue(pluginsUIMain, Pair(file, it)))
                this.guiContext.setItem(2, SimpleItem(ItemBuilder(Material.PAPER).setDisplayName(it)))
            }
            .open(player)
    }
}

class SelectAnvilListener: PacketAdapter(pluginsUIMain, PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.CLOSE_WINDOW, PacketType.Play.Server.CLOSE_WINDOW) {
    init {
        pluginsUIMain.protocolManager.addPacketListener(this)
    }

    override fun onPacketSending(e: PacketEvent) {
        e.player.removeMetadata(PLUGINS_UI_SELECT, pluginsUIMain)
    }

    override fun onPacketReceiving(e: PacketEvent) {
        if (e.packet.type == PacketType.Play.Client.CLOSE_WINDOW) {
            e.player.removeMetadata(PLUGINS_UI_SELECT, pluginsUIMain)
            return
        }

        if (!e.player.hasMetadata(PLUGINS_UI_SELECT)) return

        if (e.packet.integers.values[2] == 2) {
            val file = e.player.getMetadata(PLUGINS_UI_SELECT)[0].value() as Pair<File, String>

            Bukkit.getScheduler().runTask(pluginsUIMain, Runnable {
                e.player.closeInventory()
            })

            Bukkit.getScheduler().runTask(pluginsUIMain, Runnable {
                PluginsUI(file.first.path, e.player, file.second, file.first.name).showUI(e.player)
                e.player.playSound(e.player, Sound.BLOCK_SHULKER_BOX_OPEN, 1.0F, 1.0F)
            })
        }
    }
}