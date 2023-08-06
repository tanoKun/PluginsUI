package com.github.tanokun.pluginsui.ui.item

import com.github.tanokun.pluginsui.ui.ui.anvil.SelectAnvilUI
import com.github.tanokun.pluginsui.util.AbstractFile
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

class SelectButton(private val abstractFile: AbstractFile): AbstractItem() {
    override fun getItemProvider(): ItemProvider = ItemBuilder(Material.COMPASS)
        .setDisplayName("§a§l検索")

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        SelectAnvilUI(abstractFile.file).showUI(p)
    }
}