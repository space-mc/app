package org.space.pmmp.routes.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.space.pmmp.R
import org.space.pmmp.server.Server
import org.space.pmmp.server.ServerState
import org.space.pmmp.server.composableServerManager
import org.space.pmmp.server.process.Process

@Composable
fun ServerList() {
    val serverManager = composableServerManager()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        items(items = serverManager.servers) { server ->
            ServerListItem(server = server)
        }
    }
}

@Composable
fun ServerListItem(server: Server) {
    val serverState by remember { derivedStateOf { server.state.value } }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(size = 12.dp)
    ) {
        Column {
            ServerListItemImage(server = server, state = serverState)

            // Information
            Column(
                modifier = Modifier.padding(all = 12.dp),
                verticalArrangement = Arrangement.spacedBy(space = 12.dp)
            ) {
                // Action Buttons
                ServerListItemActions(
                    server = server,
                    serverState = serverState
                )
            }
        }
    }
}

@Composable
fun ServerListItemProcessState(serverProcess: Process) {
    val cpuUsage by remember { derivedStateOf { serverProcess.cpuUsage.value } }
    val threadCount by remember { derivedStateOf { serverProcess.threadCount.value } }
    val memoryUsage by remember { derivedStateOf { serverProcess.memoryUsage.value } }

    Row {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(weight = 1f)
        ) {
            Text(
                fontWeight = FontWeight.Bold,
                text = stringResource(id = R.string.server_process_id_label)
            )

            Text(
                fontWeight = FontWeight.Bold,
                text = stringResource(id = R.string.server_cpu_usage_label)
            )

            Text(
                fontWeight = FontWeight.Bold,
                text = stringResource(id = R.string.server_memory_usage_label)
            )

            Text(
                fontWeight = FontWeight.Bold,
                text = stringResource(id = R.string.server_thread_count_label)
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.weight(weight = 1f)
        ) {
            Text(text = "${serverProcess.pid}")

            Text(
                text = if (cpuUsage == -1L)
                    stringResource(id = R.string.uninitialized_value)
                else
                    "$cpuUsage%"
            )
            Text(
                text = if (memoryUsage == -1L)
                    stringResource(id = R.string.uninitialized_value)
                else
                    "$memoryUsage MiB"
            )
            Text(
                text = if (threadCount == -1)
                    stringResource(id = R.string.uninitialized_value)
                else
                    "$threadCount Thread${if (threadCount == 1) "" else "s"}"
            )
        }
    }
}

@Composable
private fun ServerListItemActions(
    server: Server,
    serverState: ServerState
) {
    val serverProcess by remember { derivedStateOf { server.process.value } }
    var showInformation by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            when (serverState) {
                ServerState.RUNNING ->
                    ServerListItemActionStopServer(server = server)

                ServerState.STOPPING, ServerState.STARTING, ServerState.CREATING ->
                    ServerListItemActionKillServer(server = server)

                ServerState.STOPPED, ServerState.KILLED, ServerState.FAILED ->
                    ServerListItemActionStartServer(server = server)
            }
        }

        Row {
            when (serverState) {
                ServerState.STOPPED, ServerState.KILLED, ServerState.FAILED -> Unit

                else -> ServerListActionToggleInformation {
                    showInformation = !showInformation
                }
            }

            when (serverState) {
                ServerState.STOPPED, ServerState.KILLED, ServerState.FAILED -> {
                    ServerListActionEditServer(server = server)
                    ServerListActionDeleteServer(server = server)
                }

                else -> Unit
            }
        }
    }

    if (showInformation) serverProcess?.let {
        ServerListItemProcessState(serverProcess = it)
    }
}

@Composable
private fun ServerListItemActionKillServer(
    server: Server,
) {
    Button(
        onClick = {
            server.kill()
        },

        modifier = Modifier.requiredWidth(128.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(id = R.string.stop_server_label)
            )

            Text(text = stringResource(R.string.kill_server_label))
        }
    }
}

@Composable
private fun ServerListItemActionStopServer(
    server: Server,
) {
    Button(
        onClick = {
            server.stop()
        },

        modifier = Modifier.requiredWidth(128.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Stop,
                contentDescription = stringResource(id = R.string.stop_server_label)
            )

            Text(text = stringResource(R.string.stop_server_label))
        }
    }
}

@Composable
private fun ServerListItemActionStartServer(server: Server) {
    Button(
        onClick = {
            server.start()
        },

        modifier = Modifier.requiredWidth(128.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(id = R.string.start_server_label)
            )

            Text(text = stringResource(R.string.start_server_label))
        }
    }
}

@Composable
private fun ServerListActionDeleteServer(
    server: Server,
) {
    val serverManager = composableServerManager()

    TextButton(
        onClick = {
            serverManager.removeServer(server)
        },

        modifier = Modifier.requiredWidth(48.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun ServerListActionToggleInformation(
    action: () -> Unit
) {
    TextButton(
        onClick = {
            action()
        },

        modifier = Modifier.requiredWidth(48.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}


@Composable
private fun ServerListActionEditServer(
    server: Server,
) {
    val serverManager = composableServerManager()
    var editingServer by rememberSaveable { mutableStateOf(value = false) }

    TextButton(
        onClick = {
            editingServer = true
        },

        modifier = Modifier.requiredWidth(48.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )
    }

    if (editingServer) {
        EditServerDialog(
            server = server,

            dismissDialog = {
                editingServer = false
            }
        ) { newInformation ->
            server.information.value = newInformation

            serverManager.updateServer(server)
        }
    }
}

@Composable
private fun ServerListItemImage(
    server: Server,
    state: ServerState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // TODO: Remove this padding
            .padding(all = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        // TODO: Use server banner image
        Icon(
            imageVector = Icons.Filled.Dns,
            contentDescription = stringResource(id = R.string.server_name_label),
            modifier = Modifier.size(size = 32.dp)
        )

        Text(
            fontWeight = FontWeight.Black,
            text = "${server.information().name} (${stringResource(id = state.labelResourceId)})"
        )
    }
}
