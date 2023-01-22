package com.github.tanokun.pluginsui.ui.item.operate

import de.studiocode.invui.item.ItemProvider
import de.studiocode.invui.item.builder.ItemBuilder
import de.studiocode.invui.item.impl.BaseItem
import com.github.tanokun.pluginsui.ui.ExtensionConfig
import com.github.tanokun.pluginsui.pluginsUIMain
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.metadata.FixedMetadataValue
import java.io.File

const val PLUGINS_UI_METADATA_KEY_EDIT_PASTEBIN = "PluginsUI_Download_Pastebin"

class OperateEditPastebinButton(val file: File, val player: Player): BaseItem() {

    override fun getItemProvider(): ItemProvider {
        if (file.isDirectory) return ItemBuilder(Material.AIR)
        if (!ExtensionConfig.textExtensions.contains(file.extension)) return ItemBuilder(Material.AIR)
        return ItemBuilder(Material.FEATHER)
            .addItemFlags(*ItemFlag.values())
            .addEnchantment(Enchantment.DURABILITY, 1, true)
            .setDisplayName("§b§l「pastebin」からファイルを編集する")
    }

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        if (file.isDirectory) return
        if (!ExtensionConfig.textExtensions.contains(file.extension)) return

        var permissions = ExtensionConfig.eachPermissions[Pair(p.uniqueId, file.path)] ?: ""

        var loopFile = file.parentFile
        while (loopFile != null && permissions == "") {
            permissions = ExtensionConfig.eachPermissions[Pair(p.uniqueId, loopFile.path)] ?: ""
            loopFile = loopFile.parentFile
        }

        if (!permissions.contains("EDIT") && !permissions.contains("ALL") && !(ExtensionConfig.accessPermissions[p.uniqueId] ?: arrayListOf()).contains("ALL")) {
            p.sendMessage("§c[PluginsUI] あなたはこのフォルダに対する編集権限を持っていません")
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F)
            p.closeInventory()
            return
        }

        val audience = pluginsUIMain.bukkitAudiences.player(p)

        p.setMetadata(PLUGINS_UI_METADATA_KEY_EDIT_PASTEBIN, FixedMetadataValue(pluginsUIMain, file))
        p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F)
        p.closeInventory()
        audience.sendMessage(
            Component.text("§7[PluginsUI] ファイルを編集するには「/file pastebin <id>」を入力してください" +
                    " (「id」は「https://pastebin.com/KDnFTfba」の場合「KDnFTfba」のみを入力)")
                .clickEvent(ClickEvent.suggestCommand("/file pastebin id"))
        )
    }
}