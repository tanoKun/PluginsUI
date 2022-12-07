package com.github.tanokun.pluginsui.ui.item.file

import com.github.tanokun.pluginsui.ui.ExtensionConfig

import de.studiocode.invui.item.ItemProvider
import de.studiocode.invui.item.builder.ItemBuilder
import de.studiocode.invui.item.impl.BaseItem
import com.github.tanokun.pluginsui.ui.ui.OperateUI
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.io.File

class FileButton(private val file: File, private val glowing: Boolean = false): BaseItem() {
    override fun getItemProvider(): ItemProvider {

        val extension = file.extension

        val elseItem = ItemStack(Material.PAPER)
        val itemMeta = elseItem.itemMeta ?: return ItemBuilder(Material.AIR)
        itemMeta.addItemFlags(*ItemFlag.values())
        elseItem.itemMeta = itemMeta

        val item = ExtensionConfig.extensionFormats[extension] ?: Pair(elseItem, ExtensionConfig.extensionFormats.size + 1)
        if (glowing) item.first.addUnsafeEnchantment(Enchantment.DURABILITY, 1)
        return ItemBuilder(item.first)
            .setDisplayName("§7§l${file.name}")
            .addLoreLines("§b§lクリックで操作する")
    }

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        OperateUI(file, p).showUI(p)
    }
}