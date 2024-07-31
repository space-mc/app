package org.space.pmmp.server.console

import androidx.compose.runtime.mutableStateListOf
import org.space.pmmp.server.Server

const val DELAY_TO_COMMIT_OUTPUT_LINES_MS = 150

abstract class ServerConsole(protected val server: Server) : Thread() {

    val output: MutableList<ConsoleLine> = mutableStateListOf()
    val input: MutableList<String> = mutableStateListOf()

}