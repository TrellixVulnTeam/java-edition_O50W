@file:JvmName("CommandExt")

package org.hexalite.network.kraken.extension

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import org.bukkit.command.Command
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.command.KrakenCommand
import org.hexalite.network.kraken.command.buildBukkit
import org.hexalite.network.kraken.craftbukkit

//    ____              __ __              __
//   / __/___  ___  ___/ // /  ___ _ ____ / /__
//  / _/ / -_)/ -_)/ _  // _ \/ _ `// __//  '_/
// /_/   \__/ \__/ \_,_//_.__/\_,_/ \__//_/\_\

const val Success = 1
const val Error = - 1

inline fun CommandContext<CommandSourceStack>.reply(text: String, broadcastToOps: Boolean = false) = Success.also {
    source.sendSuccess(text, broadcastToOps)
}

inline fun CommandContext<CommandSourceStack>.reply(text: Component, broadcastToOps: Boolean = false) = Success.also {
    source.sendSuccess(text, broadcastToOps)
}

inline fun CommandContext<CommandSourceStack>.reply(
    text: net.minecraft.network.chat.Component,
    broadcastToOps: Boolean = false
) = Success.also {
    source.sendSuccess(text, broadcastToOps)
}

inline fun CommandContext<CommandSourceStack>.failure(text: String) = Error.also {
    source.sendFailure(PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(text)))
}

inline fun CommandContext<CommandSourceStack>.failure(text: Component) = Error.also {
    source.sendFailure(PaperAdventure.asVanilla(text))
}

inline fun CommandContext<CommandSourceStack>.failure(text: net.minecraft.network.chat.Component) = Error.also {
    source.sendFailure(text)
}

//    ____                  __  _
//   / __/_ _________ ___  / /_(_)__  ___  ___
//  / _/ \ \ / __/ -_) _ \/ __/ / _ \/ _ \(_-<
// /___//_\_\\__/\__/ .__/\__/_/\___/_//_/___/
//                 /_/

inline fun noPlayerFound(): Nothing = throw EntityArgument.NO_PLAYERS_FOUND.create()

//   _____                              __
//  / ___/__  __ _  __ _  ___ ____  ___/ /__
// / /__/ _ \/  ' \/  ' \/ _ `/ _ \/ _  (_-<
// \___/\___/_/_/_/_/_/_/\_,_/_//_/\_,_/___/.

inline fun KrakenPlugin.registerCommand(command: Command) = server.craftbukkit().commandMap.register(namespace, command)

inline fun KrakenPlugin.registerCommand(command: KrakenCommand<CommandSourceStack>) =
    registerCommand(command.buildBukkit(this))
