package org.space.pmmp.server.impl

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import org.space.pmmp.R
import org.space.pmmp.helper.toast
import org.space.pmmp.server.Server
import org.space.pmmp.server.ServerInformation
import org.space.pmmp.server.ServerManager
import org.space.pmmp.server.ServerState
import org.space.pmmp.server.console.impl.PocketMineConsole
import org.space.pmmp.server.exec.PHPExecutable
import org.space.pmmp.server.process.Process
import java.io.File

class PocketMineServer(
    context: Context,
    manager: ServerManager,
    information: MutableState<ServerInformation>,
) : Server(context, manager, information) {

    init {
        console = PocketMineConsole(this)
    }

    override fun init(): Boolean {
        return true
    }

    override fun start(): Boolean {
        if (running.value) {
            throw IllegalStateException("The server is already running!")
        }

        val startupCommand =
            information()
                .startupCommand
                // Replace the {PHP_EXEC} with the absolute path for PHP
                .replace("{PHP_EXEC}", PHPExecutable.executablePath())

        val command = listOf("/system/bin/sh", "-c", startupCommand)
        val serverFolder = information().folder

        val folder = File(serverFolder)

        if (!folder.exists()) {
            state.value = ServerState.FAILED

            context.getString(R.string.server_failed_no_folder).toast(context)

            return false
        }

        state.value = ServerState.CREATING

        try {
            Process.create(
                command = command,
                workingDirectory = serverFolder,
                onStart = { onProcessStart(process = it) },
                onStop = { onProcessStop(process = it) }
            )
        } catch (e: Exception) {
            state.value = ServerState.FAILED

            Log.e("PocketMineServer", "An error occurred while trying to start the server", e)
        }

        return true
    }

    override fun markRunning() {
        state.value = ServerState.RUNNING
    }

    override fun markStopping() {
        state.value = ServerState.STOPPING
    }

    private fun onProcessStart(process: Process) {
        this.state.value = ServerState.STARTING
        this.running.value = true

        this.process.value = process

        this.console.startConsole()
    }

    private fun onProcessStop(process: Process) {
        this.console.stopConsole()

        this.state.value = ServerState.STOPPED
        this.running.value = false

        this.process.value = null
    }

    override fun stop(): Boolean {
        if (!running.value) {
            throw IllegalStateException("The server is not running!")
        }

        if (state.value == ServerState.STOPPING)
            return false

        console.input.add("stop")

        state.value = ServerState.STOPPING

        return true
    }

    override fun kill(): Boolean {
        if (process() == null) {
            throw IllegalStateException("The server is not running")
        }

        // Forcibly kill the server process
        process()!!.handle.destroy()
        process.value = null

        running.value = false

        state.value = ServerState.KILLED

        return true
    }

    override fun process(): Process? {
        return process.value
    }
}
