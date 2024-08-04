package org.space.pmmp.routes.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import org.space.pmmp.R
import org.space.pmmp.server.Server
import org.space.pmmp.server.ServerInformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerDialog(
    dismissDialog: () -> Unit,
    onActionButtonPressed: () -> Unit,

    title: String,
    icon: ImageVector,

    serverName: String,
    serverFolder: String,
    startupCommand: String,

    onServerNameChange: (String) -> Unit,
    onServerFolderChange: (String) -> Unit,
    onStartupCommandChange: (String) -> Unit,

    action: @Composable () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = {
            dismissDialog()
        },

        properties = DialogProperties(
            securePolicy = SecureFlagPolicy.Inherit,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        ),

        modifier = Modifier
            .widthIn(min = 280.dp, max = 560.dp)
    ) {
        Surface(
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(size = 28.dp),
        ) {
            ServerDialogContent(
                title = title,
                icon = icon,
                serverName = serverName,
                serverFolder = serverFolder,
                startupCommand = startupCommand,
                onServerFolderChange = onServerFolderChange,
                onServerNameChange = onServerNameChange,
                onStartupCommandChange = onStartupCommandChange,
                onActionButtonPressed = onActionButtonPressed,
                action = action
            )
        }
    }
}

@Composable
private fun ServerDialogContent(
    title: String,
    icon: ImageVector,

    serverName: String,
    serverFolder: String,
    startupCommand: String,

    onServerNameChange: (String) -> Unit,
    onServerFolderChange: (String) -> Unit,
    onStartupCommandChange: (String) -> Unit,

    onActionButtonPressed: () -> Unit,
    action: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 16.dp, Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "icon",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.secondary
            )

            Text(
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                lineHeight = MaterialTheme.typography.headlineSmall.lineHeight,
                text = title,
            )
        }

        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),

                singleLine = true,

                value = serverName,
                label = { Text(text = stringResource(id = R.string.server_name_label)) },
                leadingIcon = { Icon(imageVector = Icons.Filled.Dns, contentDescription = "") },

                onValueChange = onServerNameChange,
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),

                singleLine = true,

                value = serverFolder,
                label = { Text(text = stringResource(id = R.string.server_folder_label)) },
                leadingIcon = { Icon(imageVector = Icons.Filled.Folder, contentDescription = "") },

                onValueChange = onServerFolderChange,
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),

                singleLine = true,

                value = startupCommand,
                label = { Text(text = stringResource(id = R.string.startup_command_label)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Terminal,
                        contentDescription = ""
                    )
                },

                onValueChange = onStartupCommandChange,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(
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
fun CreateNewServerDialog(
    dismissDialog: () -> Unit,
    onSubmit: (ServerInformation) -> Unit
) {
    var serverName by remember { mutableStateOf(value = "New Server") }
    var serverFolder by remember { mutableStateOf(value = "/sdcard/PocketMine") }
    var startupCommand by remember { mutableStateOf(value = "{PHP_EXEC} PocketMine-MP.phar") }

    ServerDialog(
        dismissDialog = dismissDialog,

        title = stringResource(id = R.string.create_server_label),
        icon = Icons.Filled.AddCircleOutline,

        serverName = serverName,
        serverFolder = serverFolder,
        startupCommand = startupCommand,

        onServerNameChange = { serverName = it },
        onServerFolderChange = { serverFolder = it },
        onStartupCommandChange = { startupCommand = it },

        onActionButtonPressed = {
            dismissDialog()

            onSubmit(
                ServerInformation(
                    name = serverName,
                    folder = serverFolder,
                    startupCommand = startupCommand
                )
            )
        }
    ) {
        Text(
            fontSize = MaterialTheme.typography.labelLarge.fontSize,
            text = stringResource(id = R.string.add_server_button_label),
        )
    }
}

@SuppressLint("SdCardPath")
@Composable
fun EditServerDialog(
    server: Server,
    dismissDialog: () -> Unit,
    onSubmit: (ServerInformation) -> Unit
) {
    var serverName by remember { mutableStateOf(server.information().name) }
    var serverFolder by remember { mutableStateOf(server.information().folder) }
    var startupCommand by remember { mutableStateOf(server.information().startupCommand) }

    ServerDialog(
        dismissDialog = dismissDialog,

        title = stringResource(R.string.edit_server_label),
        icon = Icons.Filled.Edit,

        serverName = serverName,
        serverFolder = serverFolder,
        startupCommand = startupCommand,

        onServerNameChange = { serverName = it },
        onServerFolderChange = { serverFolder = it },
        onStartupCommandChange = { startupCommand = it },

        onActionButtonPressed = {
            dismissDialog()

            onSubmit(ServerInformation(serverName, serverFolder, startupCommand))
        }
    ) {
        Text(stringResource(R.string.save_changes_label))
    }
}
