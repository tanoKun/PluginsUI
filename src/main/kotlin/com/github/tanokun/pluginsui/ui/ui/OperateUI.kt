package com.github.tanokun.pluginsui.ui.ui

import com.github.tanokun.pluginsui.ui.AbstractUI
import com.github.tanokun.pluginsui.ui.item.operate.*
import com.github.tanokun.pluginsui.util.AbstractFile
import org.bukkit.entity.Player
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.window.Window
import java.io.File

class OperateUI(var file: File, player: Player): AbstractUI() {

    init {
        guiContext = Gui.normal()
            .setStructure(
                "a # b c d e # # f")
            .addIngredient('a', OperateDeleteButton(AbstractFile(file, player.uniqueId)))
            .addIngredient('b', OperateMoveButton(AbstractFile(file, player.uniqueId)))
            .addIngredient('c', OperateEditButton(AbstractFile(file, player.uniqueId)))
            .addIngredient('d', OperateTextDownloadPastebinButton(AbstractFile(file, player.uniqueId)))
            .addIngredient('e', OperateEditPastebinButton(AbstractFile(file, player.uniqueId)))
            .addIngredient('f', BackButton(file))
            .build()
    }

    override fun showUI(player: Player) {
        val name = if (file.isFile) "ファイル" else "フォルダ"
        Window.single()
            .setTitle("§l${name}操作: ${file.name}")
            .setGui(guiContext)
            .open(player)
    }
}