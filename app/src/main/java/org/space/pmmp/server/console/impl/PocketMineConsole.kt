package org.space.pmmp.server.console.impl

import android.os.Handler
import androidx.compose.ui.graphics.Color
import org.space.pmmp.server.Server
import org.space.pmmp.server.console.ConsoleLine
import org.space.pmmp.server.console.ServerConsole
import java.io.BufferedReader
import java.io.InputStreamReader

class PocketMineConsole(server: Server) : ServerConsole(server) {
    private var running = false
        get() = synchronized (this) { field }
        set(value) = synchronized (this) { field = value }

    private var inputConsoleThread: Thread? = null
    private var outputConsoleThread: Thread? = null

    override fun startConsole() {
        this.running = true

        this.inputConsoleThread = Thread {
            try {
                val stdin = server.process()!!.handle.outputStream

                while (this.running) {
                    var input: String

                    while ((this.input.take().also { input = it }) != null) {
                        stdin.write("$input\n".encodeToByteArray())
                        stdin.flush()
                    }
                }
            } catch (e: Exception) {
                this.stopConsole()
            }
        }

        this.outputConsoleThread = Thread {
            try {
                val handler = Handler(server.context().mainLooper)

                val stdout = server.process()!!.handle.inputStream
                val inputReader = InputStreamReader(stdout)
                val bufferedReader = BufferedReader(inputReader)

                while (this.running) {
                    var line: String

                    while (bufferedReader.readLine().also { line = it } != null) {
                        // TODO: Parsing of lines
                        this.output.add(ConsoleLine(Pair(line, Color.White)))

                        if (line.contains("or help, type \"help\" or \"?\"")) {
                            handler.post { server.markRunning() }
                        }

                        if (line.contains("[CONSOLE: Stopping the server]")) {
                            handler.post { server.markStopping() }
                        }
                    }
                }
            } catch (e: Exception) {
                this.stopConsole()
            }
        }

        this.inputConsoleThread!!.start()
        this.outputConsoleThread!!.start()
    }

    override fun stopConsole() {
        this.running = false

        if (this.inputConsoleThread != null) {
            this.inputConsoleThread!!.interrupt()

            this.inputConsoleThread = null
        }

        if (this.outputConsoleThread != null) {
            this.outputConsoleThread!!.interrupt()

            this.outputConsoleThread = null
        }
    }

}