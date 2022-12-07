package com.github.tanokun.pluginsui.util.config

import com.google.common.base.Charsets
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.*
import java.nio.charset.StandardCharsets

class ConfigEntity(private val plugin: Plugin, val file: File, private var fileName: String) {
    val config: FileConfiguration

    init {
        config = YamlConfiguration.loadConfiguration(file)
        val defConfigStream: InputStream? = plugin.getResource(fileName)

        if (defConfigStream != null) {
            config.setDefaults(YamlConfiguration.loadConfiguration(InputStreamReader(defConfigStream, StandardCharsets.UTF_8)))
            defConfigStream.close()
        }
    }

    /**
     * config.ymlのコンフィグを作成します
     * @param plugin Pluginのインスタンス
     */
    constructor(plugin: Plugin): this(plugin, File(plugin.dataFolder, "config.yml"), "config.yml")

    /**
     * 任意の名前のコンフィグを作成します
     * @param plugin Pluginのインスタンス
     * @param name 任意のファイル名
     */
    constructor(plugin: Plugin, name: String): this(plugin, File(plugin.dataFolder, name), name)

    /**
     * ファイルが存在するかの判別
     * @return 存在-> true, 不在-> false
     */
    fun isExists(): Boolean = file.exists()

    /**
     * ファイルが存在しない場合 新しく作成します
     * @return 存在-> true, 不在-> false
     */
    fun createConfig(): Boolean {
        if (file.exists()) return true
        else {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        return false
    }

    /**
     * ファイルが存在しない場合 resource内のファイルをコピーします
     * @exception IllegalArgumentException resource内にファイルがない場合
     */
    fun createDefaultConfig() {
        if (!file.exists()) plugin.saveResource(fileName, false)
    }

    /**
     * コンフィグをファイルに保存します
     */
    fun saveConfig() {
        val data: String = config.saveToString()

        val writer: Writer = OutputStreamWriter(FileOutputStream(file), Charsets.UTF_8)

        writer.use { writer ->
            writer.write(data)
        }

        writer.close()
    }
}