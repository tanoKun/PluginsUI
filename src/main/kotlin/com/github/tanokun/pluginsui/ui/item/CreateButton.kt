package com.github.tanokun.pluginsui.ui.item

import com.github.tanokun.pluginsui.ui.ui.anvil.CreateAnvilUI
import com.github.tanokun.pluginsui.util.AbstractFile
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

class CreateButton(private val abstractFile: AbstractFile): AbstractItem() {
    override fun getItemProvider(): ItemProvider = ItemBuilder(Material.ANVIL)
        .setDisplayName("§a§l新規作成")
        .addLoreLines("§7「右クリック」でフォルダ作成", "§7「左クリック」でファイル作成")

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        if (abstractFile.canCreate) {
            CreateAnvilUI(abstractFile.file, clickType.isLeftClick).showUI(p)
        } else {
            p.sendMessage("§c[PluginsUI] あなたはこのフォルダに対する作成権限を持っていません")
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F)
            p.closeInventory()
        }
    }
}