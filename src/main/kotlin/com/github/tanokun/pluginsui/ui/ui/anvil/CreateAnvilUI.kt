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

private const val PLUGINS_UI_CREATE = "PluginsUI_Create"

class CreateAnvilUI(val file: File, private val isFile: Boolean): AbstractUI() {
    init {
        guiContext = Gui.normal()
            .setStructure("x # x")
            .addIngredient('x', ItemBuilder(if (isFile) Material.PAPER else Material.BOOK).setDisplayName(if (isFile) "ファイル名を入力" else "フォルダ名を入力"))
            .build()
    }

    override fun showUI(player: Player) {
        AnvilWindow.single()
            .setGui(guiContext)
            .setTitle(if (isFile) "§lファイルを新規作成します" else "§lフォルダを新規作成します")
            .addRenameHandler {
                player.setMetadata(PLUGINS_UI_CREATE, FixedMetadataValue(pluginsUIMain, Pair(File(file, it), isFile)))
                this.guiContext.setItem(2, SimpleItem(ItemBuilder(Material.PAPER).setDisplayName(it)))
            }
            .open(player)
    }
}

class CreateAnvilListener: PacketAdapter(pluginsUIMain, PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.CLOSE_WINDOW, PacketType.Play.Server.CLOSE_WINDOW) {
    init {
        pluginsUIMain.protocolManager.addPacketListener(this)
    }

    override fun onPacketSending(e: PacketEvent) {
        e.player.removeMetadata(PLUGINS_UI_CREATE, pluginsUIMain)
    }

    override fun onPacketReceiving(e: PacketEvent) {
        if (e.packet.type == PacketType.Play.Client.CLOSE_WINDOW) {
            e.player.removeMetadata(PLUGINS_UI_CREATE, pluginsUIMain)
            return
        }

        if (!e.player.hasMetadata(PLUGINS_UI_CREATE)) return

        if (e.packet.integers.values[2] == 2) {
            val file = e.player.getMetadata(PLUGINS_UI_CREATE)[0].value() as Pair<File, Boolean>
            if (file.first.exists()) {
                Bukkit.getScheduler().runTask(pluginsUIMain, Runnable {
                    e.player.closeInventory()
                    e.player.sendMessage("§c[PluginsUI] 既に存在する名前です: ${file.first.name}")
                    e.player.playSound(e.player, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F)
                })
                return
            }

            Bukkit.getScheduler().runTask(pluginsUIMain, Runnable {
                e.player.closeInventory()
            })

            if (file.second) {
                e.player.sendMessage("§b[PluginsUI] 新規ファイルを作成中...")
                file.first.createNewFile()
                e.player.sendMessage("§b[PluginsUI] ファイル「${file.first.name}」を作成しました")
            } else {
                e.player.sendMessage("§b[PluginsUI] 新規フォルダを作成中...")
                file.first.mkdirs()
                e.player.sendMessage("§b[PluginsUI] フォルダ「${file.first.name}」を作成しました")
            }

            Bukkit.getScheduler().runTask(pluginsUIMain, Runnable {
                PluginsUI(file.first.parentFile.path, e.player, "", file.first.name).showUI(e.player)
                e.player.playSound(e.player, Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F)
            })
        }
    }
}