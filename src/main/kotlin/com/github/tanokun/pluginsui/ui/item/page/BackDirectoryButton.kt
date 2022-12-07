package com.github.tanokun.pluginsui.ui.item.page

import de.studiocode.invui.gui.impl.PagedGUI
import de.studiocode.invui.item.ItemProvider
import de.studiocode.invui.item.builder.ItemBuilder
import de.studiocode.invui.item.builder.SkullBuilder
import de.studiocode.invui.item.impl.controlitem.PageItem
import com.github.tanokun.pluginsui.ui.ui.PluginsUI
import com.github.tanokun.pluginsui.ui.ui.hasPluginsUICooltime
import com.github.tanokun.pluginsui.ui.ui.setPluginsUICooltime
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import java.io.File

const val ICE_DOWN_SKULL_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWExZGFiMTU5NGZjOTQ2ZGZlNWM5YWEyM2FkNDcwMzE0ZmMxNDBkNWU5MGYzY2JlZjE0YTEzMzM3ZTAyMDgzYSJ9fX0="

class BackDirectoryButton(private val folder: File): PageItem(true) {

    override fun getItemProvider(gui: PagedGUI): ItemProvider {
        if (folder.name == "plugins") return ItemBuilder(Material.AIR)

        return SkullBuilder(SkullBuilder.HeadTexture(ICE_DOWN_SKULL_VALUE))
            .setDisplayName("§7§l${folder.parent.replace("\\", "/")}/")
    }

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        if (folder.name == "plugins") return

        if (hasPluginsUICooltime(p)) return

        p.playSound(p, Sound.BLOCK_SHULKER_BOX_OPEN, 1.0F, 1.0F)
        PluginsUI(folder.parent ?: "", p).showUI(p)

        setPluginsUICooltime(p, 4)
    }
}