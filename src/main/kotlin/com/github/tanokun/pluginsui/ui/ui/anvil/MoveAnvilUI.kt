package com.github.tanokun.pluginsui.ui.ui.anvil

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import de.studiocode.invui.gui.builder.GUIBuilder
import de.studiocode.invui.gui.builder.guitype.GUIType
import de.studiocode.invui.item.builder.ItemBuilder
import de.studiocode.invui.window.impl.single.AnvilWindow
import com.github.tanokun.pluginsui.ui.ui.PluginsUI
import com.github.tanokun.pluginsui.pluginsUIMain
import com.github.tanokun.pluginsui.ui.AbstractUI
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import java.io.File

private const val PLUGINS_UI_RENAME = "PluginsUI_Rename"

class MoveAnvilUI(val file: File): AbstractUI() {
    init {
        guiContext = GUIBuilder(GUIType.NORMAL)
            .setStructure("x # #")
            .addIngredient('x', ItemBuilder(Material.PAPER).setDisplayName(file.name))
            .build()
    }

    override fun showUI(player: Player) {
        val name = if (file.isFile) "ファイル" else "フォルダ"
        AnvilWindow(player, "§l${name}を移動する", guiContext) {
            player.setMetadata(PLUGINS_UI_RENAME, FixedMetadataValue(pluginsUIMain, Pair(file, it)))
        }.show()
    }
}

private object RenameAnvilListener: PacketAdapter(pluginsUIMain, PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.CLOSE_WINDOW, PacketType.Play.Server.CLOSE_WINDOW) {
    init {
        pluginsUIMain.protocolManager.addPacketListener(this)
    }

    override fun onPacketSending(e: PacketEvent) {
        e.player.removeMetadata(PLUGINS_UI_RENAME, pluginsUIMain)
    }

    override fun onPacketReceiving(e: PacketEvent) {
        val p = e.player

        if (e.packet.type == PacketType.Play.Client.CLOSE_WINDOW) {
            p.removeMetadata(PLUGINS_UI_RENAME, pluginsUIMain)
            return
        }

        if (!p.hasMetadata(PLUGINS_UI_RENAME)) return

        if (e.packet.integers.values[2] == 2) {
            val file = p.getMetadata(PLUGINS_UI_RENAME)[0].value() as Pair<File, String>
            val name = if (file.first.isFile) "ファイル" else "フォルダ"

            val newFile = File(file.first.parentFile, file.second)

            if (newFile.exists()) {
                p.sendMessage("§c[PluginsUI] 移動先が既に存在しています: ${newFile.name}")
                Bukkit.getScheduler().runTask(pluginsUIMain, Runnable {
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F)
                    p.closeInventory()
                })
                return
            }

            Bukkit.getScheduler().runTask(pluginsUIMain, Runnable {
                p.playSound(p, Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F)
                p.closeInventory()
                p.sendMessage("§b[PluginsUI] ${name}を移動中...")
            })

            Bukkit.getScheduler().runTaskAsynchronously(pluginsUIMain, Runnable {
                if (file.first.isFile) FileUtils.moveFile(file.first, newFile) else FileUtils.moveDirectory(file.first, newFile)
                p.sendMessage("§b[PluginsUI] ${name}の移動を完了しました (${file.first.name} -> ${newFile.name})")
                Bukkit.getScheduler().runTask(pluginsUIMain, Runnable {
                    PluginsUI(newFile.parentFile.path, e.player, "", newFile.name).showUI(p)
                })
            })
        }
    }
}