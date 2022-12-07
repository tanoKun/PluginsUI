package com.github.tanokun.pluginsui.ui.item.file

import de.studiocode.invui.item.ItemProvider
import de.studiocode.invui.item.builder.ItemBuilder
import de.studiocode.invui.item.impl.BaseItem
import com.github.tanokun.pluginsui.ui.ui.OperateUI
import com.github.tanokun.pluginsui.ui.ui.PluginsUI
import com.github.tanokun.pluginsui.ui.ui.hasPluginsUICooltime
import com.github.tanokun.pluginsui.ui.ui.setPluginsUICooltime
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import java.io.File

class FolderButton(private val folder: File, private val glowing: Boolean = false): BaseItem() {
    override fun getItemProvider(): ItemProvider {

        return ItemBuilder(if (glowing) Material.WRITTEN_BOOK else Material.BOOK)
            .setDisplayName("§7§l${folder.name}")
            .addLoreLines("§b§l右クリックで操作する")
    }

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        if (hasPluginsUICooltime(p)) return

        if (clickType == ClickType.RIGHT) {
            OperateUI(folder, p).showUI(p)
            return
        }

        p.playSound(p, Sound.BLOCK_SHULKER_BOX_OPEN, 1.0F, 1.0F)
        PluginsUI(folder.path, p).showUI(p)

        setPluginsUICooltime(p, 4)
    }
}