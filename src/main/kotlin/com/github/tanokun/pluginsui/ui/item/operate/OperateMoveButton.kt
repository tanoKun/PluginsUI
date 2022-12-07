package com.github.tanokun.pluginsui.ui.item.operate

import de.studiocode.invui.item.ItemProvider
import de.studiocode.invui.item.builder.ItemBuilder
import de.studiocode.invui.item.impl.BaseItem
import com.github.tanokun.pluginsui.ui.ExtensionConfig
import com.github.tanokun.pluginsui.ui.ui.anvil.MoveAnvilUI
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import java.io.File

class OperateMoveButton(private val folder: File): BaseItem() {
    override fun getItemProvider(): ItemProvider {
        val name = if (folder.isFile) "ファイル" else "フォルダ"

        return ItemBuilder(Material.FEATHER)
            .setDisplayName("§b§l${name}の移動先")
    }

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        val permissions = ExtensionConfig.eachPermissions[Pair(p.uniqueId, folder.path)] ?: ""

        if (!permissions.contains("MOVE") && !permissions.contains("ALL") && !(ExtensionConfig.accessPermissions[p.uniqueId] ?: arrayListOf()).contains("ALL")) {
            p.sendMessage("§c[PluginsUI] あなたはこのフォルダに対する移動権限を持っていません")
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F)
            p.closeInventory()
            return
        }

        MoveAnvilUI(folder).showUI(p)
    }
}