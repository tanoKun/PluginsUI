package com.github.tanokun.pluginsui.ui.item

import de.studiocode.invui.item.ItemProvider
import de.studiocode.invui.item.builder.ItemBuilder
import de.studiocode.invui.item.impl.BaseItem
import com.github.tanokun.pluginsui.ui.ui.anvil.SelectAnvilUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import java.io.File

class SelectButton(val file: File): BaseItem() {
    override fun getItemProvider(): ItemProvider = ItemBuilder(Material.COMPASS)
        .setDisplayName("§a§l検索")

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        SelectAnvilUI(file).showUI(p)
    }
}