package com.github.tanokun.pluginsui.ui.item

import de.studiocode.invui.item.ItemProvider
import de.studiocode.invui.item.builder.ItemBuilder
import de.studiocode.invui.item.impl.BaseItem
import com.github.tanokun.pluginsui.ui.ExtensionConfig
import com.github.tanokun.pluginsui.ui.ui.anvil.CreateAnvilUI
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import java.io.File

class CreateButton(val file: File): BaseItem() {
    override fun getItemProvider(): ItemProvider = ItemBuilder(Material.ANVIL)
        .setDisplayName("§a§l新規作成")
        .addLoreLines("§7「右クリック」でフォルダ作成", "§7「左クリック」でファイル作成")

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {

        val permissions = ExtensionConfig.eachPermissions[Pair(p.uniqueId, file.path)] ?: ""

        if (permissions.contains("CREATE") || permissions.contains("ALL") || (ExtensionConfig.accessPermissions[p.uniqueId] ?: arrayListOf()).contains("ALL")) {
            CreateAnvilUI(file, clickType.isLeftClick).showUI(p)
        } else {
            p.sendMessage("§c[PluginsUI] あなたはこのフォルダに対する作成権限を持っていません")
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F)
            p.closeInventory()
        }
    }
}