package org.hexalite.network.kraken.logging

import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.rendering.TextColors.white
import com.github.ajalt.mordant.terminal.Terminal
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.bukkit.server
import org.hexalite.network.kraken.configuration.KrakenLoggingConfig

//    __                  _
//   / /  ___  ___ ____ _(_)__  ___ _
//  / /__/ _ \/ _ `/ _ `/ / _ \/ _ `/
// /____/\___/\_, /\_, /_/_//_/\_, /
//           /___//___/       /___/

val terminal = Terminal(tabWidth = 4, ansiLevel = AnsiLevel.TRUECOLOR)
typealias LoggingMessage = () -> Any

open class BasicLogger(val namespace: String, val config: KrakenLoggingConfig? = null) {
    open fun log(level: LoggingLevel, message: LoggingMessage? = null, exception: Throwable? = null) {
        if (config != null && !when (level) {
                LoggingLevel.System -> config.enableSystemLogLevel
                LoggingLevel.Info -> config.enableInfoLogLevel
                LoggingLevel.Warning -> config.enableWarningLogLevel
                LoggingLevel.Debug -> config.enableDebugLogLevel
                LoggingLevel.Error -> config.enableErrorLogLevel
                LoggingLevel.Critical -> config.enableCriticalLogLevel
            }
        ) return

        fun String.asFormattedText() = replace("\n", "\n${" ".repeat(level.prefix.length)}  ")

        val text = buildString {
            append(level.color(level.prefix))
            if (message != null) {
                if (level != LoggingLevel.System) {
                    append(" ${white("on $namespace")}: ")
                } else {
                    append(": ")
                }
                val message = message()
                append(
                    when (message) {
                        is Throwable -> message.stackTraceToString()
                        is Component -> LegacyComponentSerializer.legacySection().serialize(message)
                        is net.minecraft.network.chat.Component -> message.string
                        else -> terminal.render(message)
                    }.asFormattedText()
                )
            } else {
                append(": ")
            }
            if (exception != null) {
                append(exception.stackTraceToString().asFormattedText())
            }
        }
        terminal.println(text)
    }

    companion object Default: BasicLogger("Default") {
        @LoggingDsl
        fun globally(apply: KrakenLoggingConfig.() -> Unit) {
            for (plugin in server.pluginManager.plugins) {
                if (plugin is KrakenPlugin) {
                    plugin.config.logging.apply()
                }
            }
        }
    }
}

@DslMarker
annotation class LoggingDsl

inline val log get() = BasicLogger

@LoggingDsl
inline fun BasicLogger.debug(exception: Throwable? = null, noinline message: LoggingMessage? = null) =
    log(LoggingLevel.Debug, message, exception)

@LoggingDsl
inline fun BasicLogger.system(exception: Throwable? = null, noinline message: LoggingMessage? = null) =
    log(LoggingLevel.System, message, exception)

@LoggingDsl
inline fun BasicLogger.info(exception: Throwable? = null, noinline message: LoggingMessage? = null) =
    log(LoggingLevel.Info, message, exception)

@LoggingDsl
inline fun BasicLogger.warning(exception: Throwable? = null, noinline message: LoggingMessage? = null) =
    log(LoggingLevel.Warning, message, exception)

@LoggingDsl
inline fun BasicLogger.error(exception: Throwable? = null, noinline message: LoggingMessage? = null) =
    log(LoggingLevel.Error, message, exception)

@LoggingDsl
inline fun BasicLogger.critical(exception: Throwable? = null, noinline message: LoggingMessage? = null) =
    log(LoggingLevel.Critical, message, exception)