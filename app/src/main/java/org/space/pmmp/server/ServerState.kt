package org.space.pmmp.server

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.NotStarted
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayDisabled
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.filled.Stop
import androidx.compose.ui.graphics.vector.ImageVector
import org.space.pmmp.R

enum class ServerState(val labelResourceId: Int, val icon: ImageVector) {

    RUNNING (R.string.server_state_running  , Icons.Filled.PlayArrow),
    STARTING(R.string.server_state_starting , Icons.Filled.Start),
    STOPPING(R.string.server_state_stopping , Icons.Filled.PlayDisabled),
    STOPPED (R.string.server_state_stopped  , Icons.Filled.Stop),
    KILLED  (R.string.server_state_killed   , Icons.Filled.Close),
    CREATING(R.string.creating_process_label, Icons.Filled.NotStarted),
    FAILED  (R.string.server_state_failed   , Icons.Filled.Error)

}
