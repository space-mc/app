package org.space.pmmp.server

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.space.pmmp.server.process.Process
import org.space.pmmp.server.console.ServerConsole

abstract class Server(
    protected val context: Context,
    val serverManager: ServerManager,
    var information: MutableState<ServerInformation>,
) {

    protected lateinit var console: ServerConsole

    var running: MutableState<Boolean> = mutableStateOf(false)
    var state: MutableState<ServerState> = mutableStateOf(ServerState.STOPPED)

    var process: MutableState<Process?> = mutableStateOf(null)

    /**
     * This function initiate any state required before the server is able to be started
     */
    abstract fun init(): Boolean

    /**
     * This function starts the server process, this is going to return an IllegalStateException if
     * the server is already running, or if the server is not initialized properly
     */
    abstract fun start(): Boolean

    /**
     * This function stops the server process, this is going to return an IllegalStateException if
     * the server is not running, or if the server is not initialized properly
     */
    abstract fun stop(): Boolean

    /**
     * This function kills the server process, this is going to return an IllegalStateException if
     * the server is not running, or if the server is not initialized properly
     */
    abstract fun kill(): Boolean

    /**
     * This function returns the server process handle, used primarily for the ServerConsole thread
     */
    abstract fun process(): Process?

    fun information(): ServerInformation {
        return information.value
    }

    fun context(): Context {
        return context
    }

    fun serverManager(): ServerManager {
        return serverManager
    }

    fun console(): ServerConsole {
        return console
    }

    abstract fun markRunning()
    abstract fun markStopping()
}