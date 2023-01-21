package com.github.tanokun.pluginsui.ui.item.operate

import de.studiocode.invui.item.ItemProvider
import de.studiocode.invui.item.builder.ItemBuilder
import de.studiocode.invui.item.impl.BaseItem
import com.github.tanokun.pluginsui.ui.ExtensionConfig
import com.github.tanokun.pluginsui.ui.ui.PluginsUI
import com.github.tanokun.pluginsui.pluginsUIMain
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OperateDeleteButton(private val file: File): BaseItem() {
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")
    override fun getItemProvider(): ItemProvider {
        val name = if (file.isFile) "ファイル" else "フォルダ"

        return ItemBuilder(Material.BARRIER)
            .setDisplayName("§c§l${name}を削除する")
            .addLoreLines("§c§l§nシフトクリックで完全削除します。")
    }

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        val permissions = ExtensionConfig.eachPermissions[Pair(p.uniqueId, file.path)] ?: ""

        p.closeInventory()

        if (permissions.contains("DELETE") || permissions.contains("ALL") || (ExtensionConfig.accessPermissions[p.uniqueId] ?: arrayListOf()).contains("ALL")) {
            val name = if (file.isFile) "ファイル" else "フォルダ"
            Bukkit.getScheduler().runTaskAsynchronously(pluginsUIMain, Runnable {
                p.sendMessage("§b[PluginsUI] ${name}を削除中...")

                if (clickType.isShiftClick) {
                    val newFile = File(pluginsUIMain.dataFolder, "backup/${LocalDateTime.now().format(dateTimeFormatter)}-${file.name}")
                    if (file.isFile) FileUtils.moveFile(file, newFile) else FileUtils.moveDirectory(file, newFile)
                }
                if (file.isFile) FileUtils.delete(file) else FileUtils.deleteDirectory(file)
                p.sendMessage("§b[PluginsUI] ${name}の削除を終了しました")
                Bukkit.getScheduler().runTask(pluginsUIMain, Runnable {
                    p.playSound(p, Sound.BLOCK_SHULKER_BOX_OPEN, 1.0F, 1.0F)
                    PluginsUI(file.parentFile.path, p).showUI(p)
                })
            })
        } else {
            p.sendMessage("§c[PluginsUI] あなたはこのフォルダに対する削除権限を持っていません")
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F)
        }
    }
}