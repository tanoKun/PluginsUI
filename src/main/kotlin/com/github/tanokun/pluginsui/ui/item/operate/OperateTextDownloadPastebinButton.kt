package com.github.tanokun.pluginsui.ui.item.operate

import com.pastebin.api.Expiration
import com.pastebin.api.Format
import com.pastebin.api.Visibility
import com.pastebin.api.request.PasteRequest
import de.studiocode.invui.item.ItemProvider
import de.studiocode.invui.item.builder.ItemBuilder
import de.studiocode.invui.item.impl.BaseItem
import com.github.tanokun.pluginsui.ui.ExtensionConfig
import com.github.tanokun.pluginsui.ui.ui.pastebinClient
import com.github.tanokun.pluginsui.pluginsUIMain
import com.github.tanokun.pluginsui.util.io.runTaskAsync
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import java.io.File

class OperateTextDownloadPastebinButton(val file: File, val player: Player): BaseItem() {

    override fun getItemProvider(): ItemProvider {
        if (file.isDirectory) return ItemBuilder(Material.AIR)
        if (!ExtensionConfig.textExtensions.contains(file.extension)) return ItemBuilder(Material.AIR)
        return ItemBuilder(Material.FIREWORK_ROCKET)
            .setDisplayName("§b§lテキストファイルとしてダウンロードする")
    }

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        if (file.isDirectory) return
        if (!ExtensionConfig.textExtensions.contains(file.extension)) return

        val permissions = ExtensionConfig.eachPermissions[Pair(p.uniqueId, file.path)] ?: ""

        if (!permissions.contains("DOWNLOAD") && !permissions.contains("ALL") && !(ExtensionConfig.accessPermissions[p.uniqueId] ?: arrayListOf()).contains("ALL")) {
            p.sendMessage("§c[PluginsUI] あなたはこのフォルダに対するダウンロード権限を持っていません")
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F)
            p.closeInventory()
            return
        }

        val audience = pluginsUIMain.bukkitAudiences.player(p)

        p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F)
        p.closeInventory()
        p.sendMessage("§b[PluginsUI] テキストファイルをアップロード中...")

        runTaskAsync {
            val text = file.readText()
            if (text.isEmpty()) {
                p.sendMessage("§c[PluginsUI] このテキストファイルには中身がありません")
                return@runTaskAsync
            }

            val request = PasteRequest
                .content(text)
                .visibility(Visibility.UNLISTED)
                .format(Format.GETTEXT)
                .name(file.path)
                .expiration(Expiration.TEN_MINUTES)
                .build()

            val url = pastebinClient.paste(request)

            audience.sendMessage(
                Component.text("§b[PluginsUI] アップロード完了: $url")
                .clickEvent(ClickEvent.openUrl(url)))
        }
    }
}