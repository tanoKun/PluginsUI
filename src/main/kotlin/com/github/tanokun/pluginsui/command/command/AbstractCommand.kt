package com.github.tanokun.pluginsui.command.command

import dev.jorel.commandapi.CommandAPICommand

abstract class AbstractCommand(commandAPICommand: CommandAPICommand) {
    private fun init(commandAPICommand: CommandAPICommand) {
        for (method in javaClass.declaredMethods) {
            if (method.getAnnotation(SubCommand::class.java) == null) continue
            if (method.returnType != CommandAPICommand::class.java) continue
            method.isAccessible = true
            commandAPICommand.withSubcommand(method.invoke(this) as CommandAPICommand)
        }

        commandAPICommand.register()
    }
    init {
        init(commandAPICommand)
    }
}