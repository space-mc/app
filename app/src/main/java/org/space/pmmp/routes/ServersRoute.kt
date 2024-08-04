package org.space.pmmp.routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import org.space.pmmp.R
import org.space.pmmp.composableAppActivity
import org.space.pmmp.routes.components.CreateNewServerDialog
import org.space.pmmp.routes.components.ServerList
import org.space.pmmp.server.composableServerManager
import org.space.pmmp.server.impl.PocketMineServer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServersRoute(
    @Suppress("UNUSED_PARAMETER")
    navController: NavHostController
) {
    val context = composableAppActivity()

    val serverManager = composableServerManager()
    var creatingNewServer by rememberSaveable { mutableStateOf(value = false) }

    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(state = rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(connection = scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.home_screen_label)) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    creatingNewServer = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.create_server_label)
                )
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
        ) {
            if (serverManager.servers.isEmpty()) {
                NoServersCreated()
            } else {
                ServerList()
            }
        }

        if (creatingNewServer) {
            CreateNewServerDialog(
                dismissDialog = {
                    creatingNewServer = false
                }
            ) { serverInformation ->
                serverManager.addServer(
                    server = PocketMineServer(
                        context = context,
                        manager = serverManager,
                        information = mutableStateOf(serverInformation)
                    )
                )
            }
        }
    }
}

@Composable
private fun NoServersCreated() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(id = R.string.no_servers_created))
    }
}
