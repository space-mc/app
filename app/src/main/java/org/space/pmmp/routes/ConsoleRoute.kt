package org.space.pmmp.routes

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.space.pmmp.R
import org.space.pmmp.server.Server
import org.space.pmmp.server.composableServerManager
import org.space.pmmp.ui.theme.monoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun ConsoleRoute(navController: NavController) {
    val serverManager = composableServerManager()
    val servers by remember { derivedStateOf { serverManager.servers } }
    var currentServerIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        topBar = {
            Surface(
                tonalElevation = 2.dp
            ) {
                TopAppBar(
                    title = {
                        if (servers.isNotEmpty())
                            ConsoleTopBar(
                                currentServerIndex = currentServerIndex,
                                selectServer = { currentServerIndex = it },
                                servers = servers
                            )
                    }
                )
            }
        },

        bottomBar = {
            if (servers.isNotEmpty())
                ConsoleBottomBar(
                    currentServerIndex = currentServerIndex,
                    servers = servers
                )
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding()
            )
        ) {
            if (servers.isEmpty()) {
                NoServersRunning()
            } else {
                ConsoleOutput(
                    currentServerIndex = currentServerIndex,
                    servers = servers,
                )
            }
        }
    }
}

@Composable
fun NoServersRunning() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(id = R.string.no_servers_running_label))
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ConsoleOutput(currentServerIndex: Int, servers: MutableList<Server>) {
    val server by remember { derivedStateOf { servers[currentServerIndex] } }
    val consoleLines by remember { derivedStateOf { server.console().output }}
    val consoleLineState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = consoleLineState
    ) {
        items(consoleLines) { line ->
            Text(
                text = buildAnnotatedString {
                    line.components.forEach { component ->
                        withStyle (style = SpanStyle(color = component.second)) {
                            append(component.first)
                        }
                    }
                },
                fontFamily = monoFontFamily,
                fontSize = 13.sp,
            )
        }
    }

    LaunchedEffect(consoleLines.size) {
        if (consoleLines.isNotEmpty())
            consoleLineState.scrollToItem(consoleLines.size - 1)
    }
}

@Composable
fun ConsoleBottomBar(
    currentServerIndex: Int,
    servers: List<Server>
) {
    var command by rememberSaveable { mutableStateOf("") }
    val currentServer by remember { derivedStateOf { servers[currentServerIndex] } }

    Surface(tonalElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),

                singleLine = true,

                leadingIcon = { Icon(imageVector = Icons.Filled.Terminal, contentDescription = null) },

                placeholder = { Text(stringResource(id = R.string.input_command_label)) },

                value = command,
                onValueChange = { command = it },

                enabled = currentServer.running.value,

                shape = RoundedCornerShape(50),
            )

            IconButton(
                modifier = Modifier.requiredSize(56.dp),

                enabled = currentServer.running.value,

                onClick = {
                    currentServer.console().input.add(command)

                    command = ""
                },

                colors = IconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,

                    disabledContainerColor = Color(
                        MaterialTheme.colorScheme.onSurface.component1(),
                        MaterialTheme.colorScheme.onSurface.component2(),
                        MaterialTheme.colorScheme.onSurface.component3(),
                        0.12F
                    ),

                    disabledContentColor = Color(
                        MaterialTheme.colorScheme.onSurface.component1(),
                        MaterialTheme.colorScheme.onSurface.component2(),
                        MaterialTheme.colorScheme.onSurface.component3(),
                        0.38F
                    )
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ConsoleTopBar(
    selectServer: (Int) -> Unit,
    servers: List<Server>,
    currentServerIndex: Int
) {
    if (servers.isEmpty())
        return

    var expanded by rememberSaveable { mutableStateOf(false) }
    val currentServer by remember { derivedStateOf { servers[currentServerIndex] } }

    val scope = rememberCoroutineScope()

    Box { // Dropdown wrapper
        Row { // Topbar contents
            Row( // Topbar information (server name, state and clear console button)
                modifier = Modifier
                    .weight(1f)
                    .requiredHeight(56.dp)
                    .padding(end = 16.dp)
                    .clickable {
                        expanded = true
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontFamily = monoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        text = currentServer.information.value.name
                    )

                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    onClick = {
                        scope.launch {
                            currentServer.console().output.clear()
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        DropdownMenu( // The server list dropdown
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            servers.forEachIndexed { index, server ->
                DropdownMenuItem(
                    text = {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                                text = server.information().name
                            )

                            Text(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                text = server.information().folder
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.requiredSize(size = 24.dp),
                            imageVector = server.state.value.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        Text(
                            text = stringResource(id = server.state.value.labelResourceId),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    onClick = {
                        selectServer(index)

                        expanded = false
                    }
                )
            }
        }
    }
}
