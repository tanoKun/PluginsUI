package com.github.tanokun.pluginsui.ui.item.page

import de.studiocode.invui.gui.impl.PagedGUI
import de.studiocode.invui.item.ItemProvider
import de.studiocode.invui.item.builder.ItemBuilder
import de.studiocode.invui.item.builder.SkullBuilder
import de.studiocode.invui.item.impl.controlitem.PageItem
import com.github.tanokun.pluginsui.ui.ui.hasPluginsUICooltime
import com.github.tanokun.pluginsui.ui.ui.setPluginsUICooltime
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

const val WOODEN_LEFT_SKULL_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVkNzg4MjI1NzYzMTdiMDQ4ZWVhOTIyMjdjZDg1ZjdhZmNjNDQxNDhkY2I4MzI3MzNiYWNjYjhlYjU2ZmExIn19fQ=="

class BackPageButton: PageItem(true) {
    override fun getItemProvider(gui: PagedGUI): ItemProvider {
        if (!gui.hasPageBefore()) return ItemBuilder(Material.AIR)

        return SkullBuilder(SkullBuilder.HeadTexture(WOODEN_LEFT_SKULL_VALUE))
            .setDisplayName("§7前のページに行く")
            .addLoreLines("§e${gui.currentPageIndex} ページ")
    }

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        if (!gui.hasPageBefore()) return

        if (hasPluginsUICooltime(p)) return

        p.playSound(p, Sound.BLOCK_SHULKER_BOX_OPEN, 1.0F, 1.0F)
        gui.goBack()

        setPluginsUICooltime(p, 4)
    }
}