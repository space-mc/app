package org.space.pmmp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import org.space.pmmp.R
import org.space.pmmp.routes.ConsoleRoute
import org.space.pmmp.routes.NavigationRoute
import org.space.pmmp.routes.ServersRoute
import org.space.pmmp.routes.SettingsRoute
import org.space.pmmp.server.exec.BusyBoxExecutable
import org.space.pmmp.server.exec.PHPExecutable

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var hasBinaries by remember {
        mutableStateOf(
            PHPExecutable.installed(context) && BusyBoxExecutable.installed(context)
        )
    }

    if (!hasBinaries) {
        Dialog(
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ),
            onDismissRequest = { },
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    tonalElevation = 1.dp,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = stringResource(R.string.installing_binaries_label)
                    )
                }
            }
        }

        LaunchedEffect(hasBinaries) {
            scope.launch {
                PHPExecutable.extract(context)
                BusyBoxExecutable.extract(context)

                hasBinaries = true
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                BottomAppBar(modifier = Modifier) {
                    MainScreenBottomBar(navController = navController)
                }
            },
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(bottom = padding.calculateBottomPadding())
            ) {
                MainScreenRoutes(navController = navController)
            }
        }
    }
}

@Composable
fun MainScreenBottomBar(navController: NavHostController) {
    val navigationRoutes = listOf(
        NavigationRoute.Home,
        NavigationRoute.Console,
        NavigationRoute.Settings
    )

    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    var currentRoute by rememberSaveable { mutableStateOf(NavigationRoute.Home.route) }

    selectedItem = navigationRoutes.map { item -> item.route }.indexOf(currentRoute)

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp
    ) {
        navigationRoutes.forEachIndexed { index, item ->
            NavigationBarItem(
                alwaysShowLabel = false,

                icon = { Icon(item.icon!!, stringResource(item.title)) },
                label = { Text(stringResource(item.title)) },

                selected = selectedItem == index,

                onClick = {
                    selectedItem = index
                    currentRoute = item.route

                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationRoute ?: item.route) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}

@Composable
fun MainScreenRoutes(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Home.route,
    ) {
        composable(NavigationRoute.Home.route) {
            ServersRoute(navController = navController)
        }

        composable(NavigationRoute.Console.route) {
            ConsoleRoute(navController = navController)
        }

        composable(NavigationRoute.Settings.route) {
            SettingsRoute(navController = navController)
        }
    }
}

