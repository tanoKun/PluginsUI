package com.github.tanokun.pluginsui.command

import com.github.tanokun.pluginsui.command.command.AbstractCommand
import com.github.tanokun.pluginsui.command.command.SubCommand
import com.github.tanokun.pluginsui.pluginsUIMain
import com.github.tanokun.pluginsui.ui.ExtensionConfig
import com.github.tanokun.pluginsui.ui.item.operate.PLUGINS_UI_METADATA_KEY_EDIT
import com.github.tanokun.pluginsui.ui.item.operate.PLUGINS_UI_METADATA_KEY_EDIT_PASTEBIN
import com.github.tanokun.pluginsui.ui.item.operate.pluginsUIEditDownload
import com.github.tanokun.pluginsui.ui.ui.PluginsUI
import com.github.tanokun.pluginsui.ui.ui.pastebinClient
import com.github.tanokun.pluginsui.util.config.ConfigEntity
import com.github.tanokun.pluginsui.util.io.runTaskAsync
import com.github.tanokun.pluginsui.util.io.runTaskSync
import com.pastebin.api.PastebinException
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player
import java.io.File
import java.util.*

class File2Command : AbstractCommand(
    CommandAPICommand("file")
        .withPermission("plugins.command.file")
        .withArguments(TextArgument("sel"))
        .executesPlayer(PlayerCommandExecutor { player, args ->
            PluginsUI("plugins", player, args[0] as String).showUI(player)
        })
)