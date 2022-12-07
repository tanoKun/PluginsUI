package com.github.tanokun.pluginsui.ui

import de.studiocode.invui.gui.GUI
import org.bukkit.entity.Player

abstract class AbstractUI {
    lateinit var guiContext: GUI
        protected set
    
    abstract fun showUI(player: Player)
}