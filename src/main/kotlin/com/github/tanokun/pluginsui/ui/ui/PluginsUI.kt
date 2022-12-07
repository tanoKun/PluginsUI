package com.github.tanokun.pluginsui.ui.ui

import com.github.tanokun.pluginsui.pluginsUIMain
import com.github.tanokun.pluginsui.ui.AbstractUI
import com.pastebin.api.PastebinClient
import de.studiocode.invui.gui.builder.GUIBuilder
import de.studiocode.invui.gui.builder.guitype.GUIType
import de.studiocode.invui.gui.structure.Markers
import de.studiocode.invui.item.Item
import de.studiocode.invui.item.builder.ItemBuilder
import de.studiocode.invui.item.impl.SimpleItem
import de.studiocode.invui.window.impl.single.SimpleWindow
import com.github.tanokun.pluginsui.ui.ExtensionConfig
import com.github.tanokun.pluginsui.ui.item.CreateButton
import com.github.tanokun.pluginsui.ui.item.SelectButton
import com.github.tanokun.pluginsui.ui.item.file.FileButton
import com.github.tanokun.pluginsui.ui.item.file.FolderButton
import com.github.tanokun.pluginsui.ui.item.page.BackDirectoryButton
import com.github.tanokun.pluginsui.ui.item.page.BackPageButton
import com.github.tanokun.pluginsui.ui.item.page.NextPageButton
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import java.io.File

val pastebinClient: PastebinClient = PastebinClient.builder()
    .developerKey("Z1J_02cKEqkcToWZNBidXsdwuH8Jj4Gh")
    .build()

class PluginsUI(private val folderName: String, player: Player, private val select: String = "", glowing: String = ""): AbstractUI() {

    init {
        val folder = File(folderName); if (!folder.isDirectory) throw IllegalArgumentException("フォルダではありません: ${folder.name}")
        val items = ArrayList<ArrayList<Item>>()
        for (i in 0..ExtensionConfig.extensionFormats.size + 1) items.add(ArrayList())

        val permissions = ExtensionConfig.accessPermissions[player.uniqueId] ?: arrayListOf()

        for (file in folder.listFiles() ?: arrayOf()) {
            if (file.isDirectory) {
                if (select != "" && !file.name.uppercase().contains(select.uppercase())) continue
                if (permissions.any { it.startsWith(file.path) } || permissions.contains(file.parent) || permissions.contains("ALL")) {
                    items[0].add(FolderButton(file, file.name == glowing))
                }
            } else {
                if (select != "" && !file.name.uppercase().contains(select.uppercase())) continue

                if (permissions.contains(file.parent) || permissions.contains("ALL")) {
                    val extension = file.extension

                    items[ExtensionConfig.extensionFormats[extension]?.second ?: (ExtensionConfig.extensionFormats.size + 1)].add(FileButton(file, (file.name == glowing)))
                }

            }
        }

        val viewItems = mutableListOf<Item>(); items.forEach(viewItems::addAll)

        guiContext = GUIBuilder(GUIType.PAGED_ITEMS)
            .setStructure(
                "v v v v v v v v v",
                "v v v v v v v v v",
                "v v v v v v v v v",
                "v v v v v v v v v",
                "# # # # # # # # #",
                "b x d # c s o x n")
            .addIngredient('v', Markers.ITEM_LIST_SLOT_HORIZONTAL)
            .addIngredient('#', SimpleItem(ItemBuilder(Material.BLUE_STAINED_GLASS_PANE)))
            .addIngredient('n', NextPageButton())
            .addIngredient('b', BackPageButton())
            .addIngredient('d', BackDirectoryButton(folder))
            .addIngredient('c', CreateButton(folder))
            .addIngredient('s', SelectButton(folder))
            .setItems(viewItems)
            .build()
    }

    override fun showUI(player: Player) {
        val selectMessage = if (select != "") "§l検索中: $select " else "§l"
        var format = folderName.replace("\\", "/")
        val split = format.split("/")
        if (format.length > 21)
            format = ".../" + split[split.size - 1]

        SimpleWindow(player, "$selectMessage$format", guiContext).show()
    }
}

fun setPluginsUICooltime(p: Player, tick: Long) {
    p.setMetadata("PluginsUI_Cooltime", FixedMetadataValue(pluginsUIMain, true))
    Bukkit.getScheduler().runTaskLater(pluginsUIMain, Runnable { p.removeMetadata("PluginsUI_Cooltime", pluginsUIMain) }, tick)
}

fun hasPluginsUICooltime(p: Player): Boolean = p.hasMetadata("PluginsUI_Cooltime")