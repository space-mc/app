package org.space.pmmp.routes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.ui.graphics.vector.ImageVector
import org.space.pmmp.R

sealed class NavigationRoute(var route: String, val icon: ImageVector?, var title: Int) {
    data object Home : NavigationRoute("Servers", Icons.Filled.Dns, R.string.servers_route_title)
    data object Console : NavigationRoute("Console", Icons.Filled.Terminal, R.string.console_route_title)
    data object Settings : NavigationRoute("Settings", Icons.Filled.Settings, R.string.settings_route_title)
}
