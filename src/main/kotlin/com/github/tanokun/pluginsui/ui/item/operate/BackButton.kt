package com.github.tanokun.pluginsui.ui.item.operate

import de.studiocode.invui.item.ItemProvider
import de.studiocode.invui.item.builder.SkullBuilder
import de.studiocode.invui.item.impl.BaseItem
import com.github.tanokun.pluginsui.ui.item.page.WOODEN_RIGHT_SKULL_VALUE
import com.github.tanokun.pluginsui.ui.ui.PluginsUI
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import java.io.File

class BackButton(val file: File): BaseItem() {
    override fun getItemProvider(): ItemProvider {
        return SkullBuilder(SkullBuilder.HeadTexture(WOODEN_RIGHT_SKULL_VALUE))
            .setDisplayName("§7戻る")
    }

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        p.playSound(p, Sound.BLOCK_SHULKER_BOX_OPEN, 1.0F, 1.0F)
        PluginsUI(file.parentFile.path, p).showUI(p)
    }
}