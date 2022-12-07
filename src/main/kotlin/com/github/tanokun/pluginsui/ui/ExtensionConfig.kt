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
    val accessPermissions = hashMapOf<UUID, ArrayList<String>>()
    val eachPermissions = hashMapOf<Pair<UUID, String>, String>()

    init {
        val permission = ConfigEntity(pluginsUIMain, File(pluginsUIMain.dataFolder, "userPermission.yml"), "userPermission.yml")
        permission.createConfig()
        for (uuidString in permission.config.getKeys(false)) {
            val uuid = UUID.fromString(uuidString)
            val playerAccessPermissions = arrayListOf<String>()
            permission.config.getStringList(uuidString).forEach {
                if (it == "ALL") {
                    playerAccessPermissions.add("ALL")
                    return@forEach
                }
                val split = it.split(" ")
                playerAccessPermissions.add(split[0])
                eachPermissions[Pair(uuid, split[0])] = split[1]
            }
            accessPermissions[uuid] = playerAccessPermissions
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