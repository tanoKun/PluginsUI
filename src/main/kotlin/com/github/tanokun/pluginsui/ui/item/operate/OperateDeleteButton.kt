package com.github.tanokun.pluginsui.ui.item.operate

import com.github.tanokun.pluginsui.pluginsUIMain
import com.github.tanokun.pluginsui.ui.ui.PluginsUI
import com.github.tanokun.pluginsui.util.AbstractFile
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OperateDeleteButton(private val abstractFile: AbstractFile): AbstractItem() {
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")
    override fun getItemProvider(): ItemProvider {
        val name = if (abstractFile.file.isFile) "ファイル" else "フォルダ"

        return ItemBuilder(Material.BARRIER)
            .setDisplayName("§c§l${name}を削除する")
            .addLoreLines("§c§l§nシフトクリックで完全削除します。")
    }

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        p.closeInventory()

        if (abstractFile.canDelete) {
            val name = if (abstractFile.file.isFile) "ファイル" else "フォルダ"
            Bukkit.getScheduler().runTaskAsynchronously(pluginsUIMain, Runnable {
                p.sendMessage("§b[PluginsUI] ${name}を削除中...")

                if (!clickType.isShiftClick) {
                    val newFile = File(pluginsUIMain.dataFolder, "backup/${LocalDateTime.now().format(dateTimeFormatter)}-${abstractFile.file.name}")
                    newFile.parentFile.mkdir()
                    if (abstractFile.file.isFile) FileUtils.moveFile(abstractFile.file, newFile) else FileUtils.moveDirectory(abstractFile.file, newFile)
                }
                if (abstractFile.file.isFile) FileUtils.delete(abstractFile.file) else FileUtils.deleteDirectory(abstractFile.file)
                p.sendMessage("§b[PluginsUI] ${name}の削除を終了しました")
                Bukkit.getScheduler().runTask(pluginsUIMain, Runnable {
                    p.playSound(p, Sound.BLOCK_SHULKER_BOX_OPEN, 1.0F, 1.0F)
                    PluginsUI(abstractFile.file.parentFile.path, p).showUI(p)
                })
            })
        } else {
            p.sendMessage("§c[PluginsUI] あなたはこのフォルダに対する削除権限を持っていません")
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F)
        }
    }
}