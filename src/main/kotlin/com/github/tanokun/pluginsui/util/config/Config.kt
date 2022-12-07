package com.github.tanokun.pluginsui.util.config

import com.github.tanokun.pluginsui.pluginsUIMain
import com.github.tanokun.pluginsui.util.config.annotation.value.ConfigValue

abstract class Config(val configName: String, val savable: Boolean = true) {
    val config = ConfigEntity(pluginsUIMain, configName)

    fun init(instance: Config) {
        if (!config.createConfig()) {
            for (field in instance.javaClass.declaredFields) {
                field.isAccessible = true
                val configValue = if (field.getAnnotation(ConfigValue::class.java) == null) continue else field.getAnnotation(
                    ConfigValue::class.java)!!

                config.config.set(configValue.name, field.get(instance))
            }

            config.saveConfig()

        } else {
            for (field in instance.javaClass.declaredFields) {
                field.isAccessible = true
                val configValue = if (field.getAnnotation(ConfigValue::class.java) == null) continue else field.getAnnotation(
                    ConfigValue::class.java)!!
                field.set(this, config.config.get(configValue.name, field.get(instance)))
            }
        }
    }
}