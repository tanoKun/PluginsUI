package com.github.tanokun.pluginsui.ui.item.operate

import com.github.tanokun.pluginsui.pluginsUIMain
import com.github.tanokun.pluginsui.ui.ExtensionConfig
import com.github.tanokun.pluginsui.ui.ui.PluginsUI
import com.github.tanokun.pluginsui.util.AbstractFile
import com.github.tanokun.pluginsui.util.io.runTaskSync
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import okhttp3.*
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.metadata.FixedMetadataValue
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem
import java.io.File
import java.io.IOException
import java.net.SocketException
import kotlin.math.floor
import kotlin.math.pow

const val PLUGINS_UI_METADATA_KEY_EDIT = "PluginsUI_Download"

class OperateEditButton(private val abstractFile: AbstractFile): AbstractItem() {

    override fun getItemProvider(): ItemProvider {
        if (abstractFile.file.isDirectory) return ItemBuilder(Material.AIR)
        return ItemBuilder(Material.QUARTZ)
            .setDisplayName("§b§lファイルを編集する")
    }

    override fun handleClick(clickType: ClickType, p: Player, e: InventoryClickEvent) {
        if (abstractFile.file.isDirectory) return

        if (!abstractFile.canEdit) {
            p.sendMessage("§c[PluginsUI] あなたはこのフォルダに対する編集権限を持っていません")
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F)
            p.closeInventory()
            return
        }

        val audience = pluginsUIMain.bukkitAudiences.player(p)

        p.setMetadata(PLUGINS_UI_METADATA_KEY_EDIT, FixedMetadataValue(pluginsUIMain, abstractFile.file))
        p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F)
        p.closeInventory()
        audience.sendMessage(
            Component.text("§7[PluginsUI] ファイルを編集するには「/file edit \"<url>\"」からURLを指定してください (メッセージクリックで入力できます)")
                .clickEvent(ClickEvent.suggestCommand("/file edit \"url\""))
        )
    }
}

fun pluginsUIEditDownload(file: File, url: String, p: Player) {
    val audience = pluginsUIMain.bukkitAudiences.player(p)

    val bossBar = BossBar.bossBar(Component.text("§7§lURLに接続中..."), 0F, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS)
    audience.showBossBar(bossBar)

    val okHttpClient = OkHttpClient.Builder().build()

    val request: Request

    try {
        request = Request.Builder()
            .url(url)
            .get()
            .build()
    } catch (e: IllegalArgumentException) {
        p.sendMessage("§c[PluginsUI] URLは「http」、もしくは「https」で始まる必要があります")
        audience.hideBossBar(bossBar)
        return
    }

    okHttpClient.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            if (e is SocketException) {
                p.sendMessage("§c[PluginsUI] URLに接続できませんでした。時間を実行するか、確認をしてください")
            } else {
                p.sendMessage("§c[PluginsUI] ダウンロード時に不明なエラーが発生しました。コンソールを確認してください")
                e.printStackTrace()
            }

            audience.hideBossBar(bossBar)
        }

        override fun onResponse(call: Call, response: Response) {

            if (!response.isSuccessful) {
                p.sendMessage("§c[PluginsUI] URLが存在しないか、接続することができません")
                audience.hideBossBar(bossBar)
                return
            }

            val outputStream = file.outputStream()
            val inputStream = response.body?.byteStream()
            val buff = ByteArray(1024 * 10)
            val target: Long? = response.body?.contentLength()
            var progress = 0L

            if ((target ?: -1) == -1L) {
                p.sendMessage("§c[PluginsUI] そのURLからはダウンロードができません")
                audience.hideBossBar(bossBar)
                inputStream?.close()
                outputStream.close()
                return
            }

            if ((target ?: 0) < ExtensionConfig.maxDownloadByteSize) {
                p.sendMessage("§c[PluginsUI] ファイルの容量が大きすぎるそうです")
                audience.hideBossBar(bossBar)
                inputStream?.close()
                outputStream.close()
                return
            }

            try {

                while (true) {
                    val read = inputStream?.read(buff)
                    if (read == -1 || read == null || target == null) {
                        break
                    }

                    progress += read
                    outputStream.write(buff, 0, read)

                    val percentage = progress.toFloat() / target.toFloat()
                    bossBar.name(Component.text("§7§l${(percentage * 100).toInt()}% (${sizeFormat(progress)}/${sizeFormat(target)})"))
                    bossBar.progress(percentage)
                }

                Thread.sleep(1000)

                p.sendMessage("§b[PluginsUI] ダウンロードが完了しました")
                audience.hideBossBar(bossBar)
                runTaskSync {
                    PluginsUI(file.parentFile.path, p, "", file.name).showUI(p)
                }

                inputStream?.close()
                outputStream.close()
            } catch (e: Exception) {
                p.sendMessage("§c[PluginsUI] ダウンロード時に不明なエラーが発生しました。コンソールを確認してください")
                e.printStackTrace()
                audience.hideBossBar(bossBar)
            }
        }
    })
}

private fun sizeFormat(size: Long): String {
    val kb = 1024.0
    val mb = kb.pow(2.0)
    val gb = kb.pow(3.0)
    val tb = kb.pow(4.0)

    return if (size > tb) "${floor((size / tb) * 10) / 10}TB"
        else if (size > gb) "${floor((size / gb) * 10) / 10}GB"
        else if (size > mb) "${floor((size / mb) * 10) / 10}MB"
        else "${floor((size / kb) * 10) / 10}KB"
}