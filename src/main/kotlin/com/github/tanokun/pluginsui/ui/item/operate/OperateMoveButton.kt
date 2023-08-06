package com.github.tanokun.pluginsui.ui.item.operate

import com.github.tanokun.pluginsui.ui.ui.anvil.MoveAnvilUI
import com.github.tanokun.pluginsui.util.AbstractFile
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

class OperateMoveButton(private val abstractFile: AbstractFile): AbstractItem() {
    override fun getItemProvider(): ItemProvider {
        val name = if (abstractFile.file.isFile) "ファイル" else "フォルダ"

        return ItemBuilder(Material.FEATHER)
            .setDisplayName("§b§l${name}の移動先")
    }

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        if (!abstractFile.canMove) {
            p.sendMessage("§c[PluginsUI] あなたはこのフォルダに対する移動権限を持っていません")
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F)
            p.closeInventory()
            return
        }

        MoveAnvilUI(abstractFile.file).showUI(p)
    }
}