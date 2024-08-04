package org.space.pmmp.server.console

import androidx.compose.runtime.mutableStateListOf
import org.space.pmmp.server.Server
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

abstract class ServerConsole(protected val server: Server) {

    val output: MutableList<ConsoleLine> = mutableStateListOf()
    val input: BlockingQueue<String> = LinkedBlockingQueue()

    abstract fun startConsole()

    abstract fun stopConsole()

}