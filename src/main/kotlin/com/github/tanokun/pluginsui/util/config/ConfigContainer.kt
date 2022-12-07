package com.github.tanokun.pluginsui.util.config

import com.github.tanokun.pluginsui.util.getClasses

class ConfigContainer(rootClass: Class<*>) {
    private val configs = HashMap<String, Config>()

    init {
        val classes = getClasses(rootClass)

        for (clazz in classes) {
            if (clazz.superclass != Config::class.java) continue
            val instance = clazz.getField("INSTANCE").get(null) as Config

            configs[instance.configName] = instance

            instance.init(instance)
        }
    }

    fun save() {
        configs.values.forEach { if (it.savable) it.config.saveConfig() }
    }
}