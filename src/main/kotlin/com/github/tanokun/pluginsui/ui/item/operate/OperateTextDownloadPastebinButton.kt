package com.github.tanokun.pluginsui.ui.item.operate

import com.github.tanokun.pluginsui.pluginsUIMain
import com.github.tanokun.pluginsui.ui.ExtensionConfig
import com.github.tanokun.pluginsui.ui.ui.pastebinClient
import com.github.tanokun.pluginsui.util.AbstractFile
import com.github.tanokun.pluginsui.util.io.runTaskAsync
import com.pastebin.api.Expiration
import com.pastebin.api.Format
import com.pastebin.api.Visibility
import com.pastebin.api.request.PasteRequest
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

class OperateTextDownloadPastebinButton(private val abstractFile: AbstractFile): AbstractItem() {

    override fun getItemProvider(): ItemProvider {
        if (abstractFile.file.isDirectory) return ItemBuilder(Material.AIR)
        if (!ExtensionConfig.textExtensions.contains(abstractFile.file.extension)) return ItemBuilder(Material.AIR)
        return ItemBuilder(Material.FIREWORK_ROCKET)
            .setDisplayName("§b§lテキストファイルとしてダウンロードする")
    }

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        if (abstractFile.file.isDirectory) return
        if (!ExtensionConfig.textExtensions.contains(abstractFile.file.extension)) return

        if (!abstractFile.canDownload) {
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
            val text = abstractFile.file.readText()
            if (text.isEmpty()) {
                p.sendMessage("§c[PluginsUI] このテキストファイルには中身がありません")
                return@runTaskAsync
            }

            val request = PasteRequest
                .content(text)
                .visibility(Visibility.UNLISTED)
                .format(Format.GETTEXT)
                .name(abstractFile.file.path)
                .expiration(Expiration.TEN_MINUTES)
                .build()

            val url = pastebinClient.paste(request)

            audience.sendMessage(
                Component.text("§b[PluginsUI] アップロード完了: $url")
                .clickEvent(ClickEvent.openUrl(url)))
        }
    }
}