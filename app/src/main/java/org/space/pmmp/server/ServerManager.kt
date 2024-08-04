package org.space.pmmp.server

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import org.space.pmmp.server.impl.PocketMineServer

class ServerManager(private val context: Context) {

    val servers: MutableList<Server> = mutableStateListOf()

    fun addServer(server: Server) {
        servers.add(server)

        save(context)
    }

    fun updateServer(server: Server) {
        servers.remove(server)
        servers.add(server)

        save(context)
    }

    fun removeServer(server: Server) {
        servers.remove(server)

        save(context)
    }

    fun save(context: Context) {
        Log.d("ServerManager", "Saving servers... (context is $context)")

        val sharedPrefs = context.getSharedPreferences("ServerList", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        editor.putString(
            "Servers",
            servers.joinToString(separator = "\u0001") { it.information().name }
        )

        servers.forEach { server ->
            Log.d("ServerManager", "Saving server $server")

            editor.putString(
                "${server.information().name}:folder",
                server.information().folder
            )

            editor.putString(
                "${server.information().name}:startupCommand",
                server.information().startupCommand
            )
        }

        editor.apply()

        Log.d("ServerManager", "Saved ${this.servers.size} servers")
    }

    fun load(context: Context) {
        Log.d("ServerManager", "Loading servers... (context is $context)")

        val sharedPrefs = this.context.getSharedPreferences("ServerList", Context.MODE_PRIVATE)
        val servers = sharedPrefs.getString("Servers", null)

        this.servers.clear()

        if (servers == null) {
            Log.d("ServerManager", "There are no servers saved")
            return
        }

        servers.split("\u0001").forEach { serverName ->
            Log.d("ServerManager", "Loading server $serverName")

            try {
                val folder = sharedPrefs.getString("$serverName:folder", null)!!
                val startupCommand = sharedPrefs.getString("$serverName:startupCommand", null)!!

                val information = ServerInformation(
                    name = serverName,
                    folder = folder,
                    startupCommand = startupCommand
                )

                this.servers.add(
                    PocketMineServer(
                        context = context,
                        manager = this,
                        information = mutableStateOf(information)
                    )
                )
            } catch (_: Exception) {
            }
        }

        Log.d("ServerManager", "Loaded ${this.servers.size} servers")
    }

}

var LocalServerManager: ProvidableCompositionLocal<ServerManager?> = compositionLocalOf { null }

@Composable
fun composableServerManager(): ServerManager {
    return LocalServerManager.current!!
}