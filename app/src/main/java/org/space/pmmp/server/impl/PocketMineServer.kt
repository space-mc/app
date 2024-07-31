package org.space.pmmp.server.impl

import android.content.Context
import org.space.pmmp.server.Server
import org.space.pmmp.server.ServerInformation
import org.space.pmmp.server.ServerManager
import org.space.pmmp.server.ServerState
import org.space.pmmp.server.console.ServerConsole

class PocketMineServer(
    context: Context,
    serverManager: ServerManager,
    information: ServerInformation,
    console: ServerConsole
) : Server(context, serverManager, information, console) {

    private var process: Process? = null

    override fun init(): Boolean {
        TODO("Not yet implemented")
    }

    override fun start(): Boolean {
        TODO("Not yet implemented")
    }

    override fun stop(): Boolean {
        TODO("Not yet implemented")
    }

    override fun kill(): Boolean {
        if (process == null) {
            throw IllegalStateException("The server is not running")
        }

        state.value = ServerState.KILLED

        // Forcibly kill the server process
        process!!.destroy()
        process = null

        return true
    }

    override fun process(): Process {
        if (process == null) {
            throw IllegalStateException("The server is not running")
        }

        return process!!
    }
}