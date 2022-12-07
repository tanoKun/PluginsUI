package com.github.tanokun.pluginsui.command

import com.github.tanokun.pluginsui.pluginsUIMain
import com.pastebin.api.PastebinException
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import com.github.tanokun.pluginsui.command.command.AbstractCommand
import com.github.tanokun.pluginsui.command.command.SubCommand
import com.github.tanokun.pluginsui.ui.item.operate.PLUGINS_UI_METADATA_KEY_EDIT
import com.github.tanokun.pluginsui.ui.item.operate.PLUGINS_UI_METADATA_KEY_EDIT_PASTEBIN
import com.github.tanokun.pluginsui.ui.item.operate.pluginsUIEditDownload
import com.github.tanokun.pluginsui.ui.ui.PluginsUI
import com.github.tanokun.pluginsui.ui.ui.pastebinClient
import com.github.tanokun.pluginsui.util.io.runTaskAsync
import com.github.tanokun.pluginsui.util.io.runTaskSync
import org.bukkit.entity.Player
import java.io.File

class FileCommand : AbstractCommand(
    CommandAPICommand("file")
        .withPermission("plugins.command.file")
        .executesPlayer(PlayerCommandExecutor {player, _ ->
            PluginsUI("plugins", player).showUI(player)
        })
) {
    @SubCommand
    private fun edit(): CommandAPICommand {
        return CommandAPICommand("edit")
            .withArguments(TextArgument("url"))
            .executesPlayer(PlayerCommandExecutor { p: Player, args: Array<Any> ->
                if (!p.hasMetadata(PLUGINS_UI_METADATA_KEY_EDIT)) {
                    p.sendMessage("§c[PluginsUI] ダウンロードするファイルの対象が見つかりません。UIから選択してください")
                    return@PlayerCommandExecutor
                }

                val url = args[0] as String
                val file = p.getMetadata(PLUGINS_UI_METADATA_KEY_EDIT)[0].value() as File

                p.removeMetadata(PLUGINS_UI_METADATA_KEY_EDIT, pluginsUIMain)

                p.sendMessage("§b[PluginsUI] ファイルをダウンロード中...")

                pluginsUIEditDownload(file, url, p)
            })
    }

    @SubCommand
    private fun pastebin(): CommandAPICommand {
        return CommandAPICommand("pastebin")
            .withArguments(TextArgument("id"))
            .executesPlayer(PlayerCommandExecutor { p: Player, args: Array<Any> ->
                if (!p.hasMetadata(PLUGINS_UI_METADATA_KEY_EDIT_PASTEBIN)) {
                    p.sendMessage("§c[PluginsUI] ダウンロードするファイルの対象が見つかりません。UIから選択してください")
                    return@PlayerCommandExecutor
                }

                val id = args[0] as String
                val file = p.getMetadata(PLUGINS_UI_METADATA_KEY_EDIT_PASTEBIN)[0].value() as File

                p.removeMetadata(PLUGINS_UI_METADATA_KEY_EDIT_PASTEBIN, pluginsUIMain)

                p.sendMessage("§b[PluginsUI] ファイルをpastebinからダウンロード中...")

                runTaskAsync {
                    try {
                        val raw = pastebinClient.getPaste(id) ?: ""
                        if (raw.contains("<title>Pastebin.com - Not Found (#404)</title>")) {
                            p.sendMessage("§c[PluginsUI] 「https://pastebin.com/$id」に接続できませんでした")
                            return@runTaskAsync
                        }
                        val outputStream = file.outputStream()
                        outputStream.write(raw.toByteArray())
                        outputStream.close()
                        p.sendMessage("§b[PluginsUI] ダウンロードが完了しました")
                        runTaskSync {
                            PluginsUI(file.parentFile.path, p, "", file.name).showUI(p)
                        }
                    } catch (e: PastebinException) {
                        e.printStackTrace()
                    }
                }
            })
    }
}