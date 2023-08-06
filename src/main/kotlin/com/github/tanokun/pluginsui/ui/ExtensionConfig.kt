package com.github.tanokun.pluginsui.ui

import com.github.tanokun.pluginsui.pluginsUIMain
import com.github.tanokun.pluginsui.util.config.Config
import com.github.tanokun.pluginsui.util.config.ConfigEntity
import com.github.tanokun.pluginsui.util.config.annotation.value.ConfigValue
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*

object ExtensionConfig: Config("extensionConfig.yml", true) {
    val accessPermissions = hashMapOf<UUID, List<FilePermission>>()

    init {
        val permission = ConfigEntity(pluginsUIMain, File(pluginsUIMain.dataFolder, "userPermission.yml"), "userPermission.yml")
        permission.createConfig()
        for (uuidString in permission.config.getKeys(false)) {
            val uuid = UUID.fromString(uuidString)
            accessPermissions[uuid] = permission.config.getStringList(uuidString).map { FilePermission(it) }
        }
    }

    @ConfigValue("extensionFormats")
    private var savableExtensionFormats = arrayListOf(
        "jar ENCHANTED_BOOK|FALSE",
        "txt FLOWER_BANNER_PATTERN|FALSE",
        "yml MAP|FALSE",
        "sk HEART_OF_THE_SEA|FALSE",
        "png PAINTING|FALSE",
        "db MUSIC_DISC_STAL|FALSE",
    )

    @ConfigValue("textExtensions")
    var textExtensions = arrayListOf("yml", "txt", "sk", "")

    @ConfigValue("maxDownloadByteSize")
    var maxDownloadByteSize = 625000000L

    val extensionFormats = lazy {
        val value = HashMap<String, Pair<ItemStack, Int>>()
        savableExtensionFormats.withIndex().forEach() {
            val split = it.value.split(" ")
            val itemData = split[1].split("|")
            val glowing = itemData[1].toBoolean()

            val item = ItemStack(Material.valueOf(itemData[0]))
            val itemMeta = item.itemMeta ?: return@forEach
            itemMeta.addItemFlags(*ItemFlag.values())
            item.itemMeta = itemMeta

            if (glowing) item.addUnsafeEnchantment(Enchantment.DURABILITY, 1)

            value[split[0]] = Pair(item, it.index + 1)
        }

        return@lazy value
    }.value
}

class FilePermission(raw: String) {
    val path: String

    val canCreate: Boolean = raw.contains("CREATE") || raw.contains("ALL")

    val canMove: Boolean = raw.contains("MOVE") || raw.contains("ALL")

    val canDelete: Boolean = raw.contains("DELETE") || raw.contains("ALL")

    val canEdit: Boolean = raw.contains("EDIT") || raw.contains("ALL")

    val canDownload: Boolean = raw.contains("DOWNLOAD") || raw.contains("ALL")

    init {
        path = raw.split(" ")[0]
    }
}