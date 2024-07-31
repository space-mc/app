package org.space.pmmp.server

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateMapOf

class ServerManager(private val context: Context) {

    val servers: MutableMap<String, Server> = mutableStateMapOf()

    fun addServer(server: Server) {
        servers[server.information.name] = server
    }

    fun removeServer(server: Server) {
        servers.remove(server.information.name)
    }

}

var LocalServerManager: ProvidableCompositionLocal<ServerManager?> = compositionLocalOf { null }

@Composable
fun composableServerManager(): ServerManager {
    return LocalServerManager.current!!
}