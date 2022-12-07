package com.github.tanokun.pluginsui.ui.ui

import com.github.tanokun.pluginsui.ui.AbstractUI
import com.github.tanokun.pluginsui.ui.item.operate.OperateEditPastebinButton
import com.github.tanokun.pluginsui.ui.item.operate.OperateMoveButton
import com.github.tanokun.pluginsui.ui.item.operate.OperateTextDownloadPastebinButton
import de.studiocode.invui.gui.builder.GUIBuilder
import de.studiocode.invui.gui.builder.guitype.GUIType
import de.studiocode.invui.window.impl.single.SimpleWindow
import com.github.tanokun.pluginsui.ui.item.operate.*
import com.github.tanokun.pluginsui.ui.item.operate.OperateEditButton
import org.bukkit.entity.Player
import java.io.File

class OperateUI(var file: File, player: Player): AbstractUI() {

    init {
        guiContext = GUIBuilder(GUIType.NORMAL)
            .setStructure(
                "a # b c d e # # f")
            .addIngredient('a', OperateDeleteButton(file))
            .addIngredient('b', OperateMoveButton(file))
            .addIngredient('c', OperateEditButton(file))
            .addIngredient('d', OperateTextDownloadPastebinButton(file, player))
            .addIngredient('e', OperateEditPastebinButton(file, player))
            .addIngredient('f', BackButton(file))
            .build()
    }

    override fun showUI(player: Player) {
        val name = if (file.isFile) "ファイル" else "フォルダ"
        SimpleWindow(player, "§l${name}操作: ${file.name}", guiContext).show()
    }
}