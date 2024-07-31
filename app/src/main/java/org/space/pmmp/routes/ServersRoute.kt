package org.space.pmmp.routes

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.navigation.NavHostController
import org.space.pmmp.R
import org.space.pmmp.server.ServerInformation
import org.space.pmmp.server.composableServerManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServersRoute(navController: NavHostController) {
    val serverManager = composableServerManager()
    val serverRunning by remember { mutableStateOf(false) }

    var creatingNewServer by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text(stringResource(R.string.home_screen_label)) })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    creatingNewServer = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.create_server_label)
                )
            }
        }
    ) { padding ->
        if (serverManager.servers.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(
                        PaddingValues(
                            8.dp,
                            padding.calculateTopPadding(),
                            8.dp,
                            8.dp
                        )
                    )
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(R.string.no_servers_created))
            }
        } else {
            LazyColumn(modifier = Modifier.padding(
                PaddingValues(
                    8.dp,
                    padding.calculateTopPadding(),
                    8.dp,
                    8.dp
                )
            )) {
                /*
                Surface(
                    tonalElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(24.dp, Dp.Unspecified),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(PaddingValues(12.dp))) {
                        Text(
                            text = if (serverRunning)
                                stringResource(R.string.server_running_label)
                            else
                                stringResource(R.string.server_not_running_label)
                        )

                        Spacer(Modifier.padding(4.dp))

                        Row(Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    // serverManager.start(context)
                                },
                                enabled = !serverRunning,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(R.string.start_server_label))
                            }

                            Spacer(modifier = Modifier.padding(4.dp))

                            Button(
                                onClick = {
                                    // serverManager.stop(context, false)
                                },
                                enabled = serverRunning,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(R.string.stop_server_label))
                            }
                        }
                    }
                }*/
            }
        }

        if (creatingNewServer) {
            CreateNewServerSheet(dismissSheet = { creatingNewServer = false }) {
                Log.i("ServerInfo", "Info: $it")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerSheet(
    dismissSheet: () -> Unit,
    onActionButtonPressed: () -> Unit,

    title: String,

    serverName: String,
    serverFolder: String,
    startupCommand: String,

    onServerNameChange: (String) -> Unit,
    onServerFolderChange: (String) -> Unit,
    onStartupCommandChange: (String) -> Unit,

    action: @Composable () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = {
            dismissSheet()
        },

        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = true,
            securePolicy = SecureFlagPolicy.Inherit,
            isFocusable = true
        ),

        tonalElevation = 3.dp,
        sheetMaxWidth = 480.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                text = title
            )
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),

                singleLine = true,

                value = serverName,
                label = { Text(stringResource(R.string.server_name_label)) },
                leadingIcon = { Icon(Icons.Filled.Dns, "") },

                onValueChange = onServerNameChange,
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),

                singleLine = true,

                value = serverFolder,
                label = { Text(stringResource(R.string.server_folder_label)) },
                leadingIcon = { Icon(Icons.Filled.Folder, "") },

                onValueChange = onServerFolderChange,
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),

                singleLine = true,

                value = startupCommand,
                label = { Text(stringResource(R.string.startup_command_label)) },
                leadingIcon = { Icon(Icons.Filled.Terminal, "") },

                onValueChange = onStartupCommandChange,
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onActionButtonPressed()
                },
                content = { action() }
            )
        }
    }
}

@SuppressLint("SdCardPath")
@Composable
fun CreateNewServerSheet(
    dismissSheet: () -> Unit,
    onSubmit: (ServerInformation) -> Unit
) {
    var serverName by remember { mutableStateOf("New Server") }
    var serverFolder by remember { mutableStateOf("/sdcard/PocketMine") }
    var startupCommand by remember { mutableStateOf("{PHP_EXEC} PocketMine-MP.phar") }

    ServerSheet(
        dismissSheet = dismissSheet,

        title = stringResource(R.string.create_server_label),

        serverName = serverName,
        serverFolder = serverFolder,
        startupCommand = startupCommand,

        onServerNameChange = { serverName = it },
        onServerFolderChange = { serverFolder = it },
        onStartupCommandChange = { startupCommand = it },

        onActionButtonPressed = {
            dismissSheet()

            onSubmit(ServerInformation(serverName, serverFolder, startupCommand))
        }
    ) {
        Icon(Icons.Filled.Add, stringResource(R.string.add_server_button_label))
        Text(stringResource(R.string.add_server_button_label))
    }
}

@Composable
fun EditServerSheet(
    dismissSheet: () -> Unit,
    onSubmit: (ServerInformation) -> Unit
) {
    var serverName by remember { mutableStateOf("New Server") }
    var serverFolder by remember { mutableStateOf("/sdcard/PocketMine") }
    var startupCommand by remember { mutableStateOf("{PHP_EXEC} PocketMine-MP.phar") }

    ServerSheet(
        dismissSheet = dismissSheet,

        title = stringResource(R.string.create_server_label),

        serverName = serverName,
        serverFolder = serverFolder,
        startupCommand = startupCommand,

        onServerNameChange = { serverName = it },
        onServerFolderChange = { serverFolder = it },
        onStartupCommandChange = { startupCommand = it },

        onActionButtonPressed = {
            dismissSheet()

            onSubmit(ServerInformation(serverName, serverFolder, startupCommand))
        }
    ) {
        Icon(Icons.Filled.Add, stringResource(R.string.add_server_button_label))
        Text(stringResource(R.string.add_server_button_label))
    }
}