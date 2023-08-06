package com.github.tanokun.pluginsui.ui.item.operate

import com.github.tanokun.pluginsui.ui.item.page.WOODEN_RIGHT_SKULL_VALUE
import com.github.tanokun.pluginsui.ui.ui.PluginsUI
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.SkullBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem
import java.io.File

class BackButton(val file: File): AbstractItem() {
    override fun getItemProvider(): ItemProvider {
        return SkullBuilder(SkullBuilder.HeadTexture(WOODEN_RIGHT_SKULL_VALUE))
            .setDisplayName("§7戻る")
    }

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        p.playSound(p, Sound.BLOCK_SHULKER_BOX_OPEN, 1.0F, 1.0F)
        PluginsUI(file.parentFile.path, p).showUI(p)
    }
}