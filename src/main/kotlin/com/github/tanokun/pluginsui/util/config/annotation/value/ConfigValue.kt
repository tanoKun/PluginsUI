package com.github.tanokun.pluginsui.util.config.annotation.value

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ConfigValue(val name: String)
