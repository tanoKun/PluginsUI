package com.github.tanokun.pluginsui.ui

import org.bukkit.entity.Player
import xyz.xenondevs.invui.gui.Gui

abstract class AbstractUI {
    lateinit var guiContext: Gui
        protected set
    
    abstract fun showUI(player: Player)
}