package org.space.pmmp.routes

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.space.pmmp.R


@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun ConsoleRoute(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { ConsoleBottomBar() }
    ) { padding ->
        ConsoleOutput(padding)
    }
}

@Composable
fun ConsoleOutput(padding: PaddingValues) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(
                bottom = padding.calculateBottomPadding()
            )),
        state = listState
    ) {
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ConsoleBottomBar() {
    var command by remember { mutableStateOf("") }

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)) {
            TextField(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(0.dp),
                value = command,
                singleLine = true,

                placeholder = {
                    Text(
                        stringResource(R.string.insert_command_label)
                    )
                },
                onValueChange = { newValue ->
                    command = newValue
                }
            )

            Button(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(56.dp),
                contentPadding = PaddingValues(16.dp),
                shape = RoundedCornerShape(0.dp),

                onClick = {
                    command = ""
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = stringResource(R.string.insert_command_label),
                )
            }
        }
    }
}