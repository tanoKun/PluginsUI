package com.github.tanokun.pluginsui.util.io

import com.github.tanokun.pluginsui.pluginsUIMain
import org.bukkit.Bukkit

fun runTaskAsync(func: () -> Unit) {
    Bukkit.getScheduler().runTaskAsynchronously(pluginsUIMain, func)
}

fun runTaskSync(func: () -> Unit) {
    Bukkit.getScheduler().runTask(pluginsUIMain, func)
}