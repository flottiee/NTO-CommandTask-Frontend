package ru.myitschool.work.ui.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import ru.myitschool.work.core.TestIds
import ru.myitschool.work.ui.nav.BookScreenDestination
import ru.myitschool.work.ui.nav.AuthScreenDestination

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.actionFlow.collect {
            navController.navigate(AuthScreenDestination) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    
    // To handle backstack correctly after booking
    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.onIntent(MainIntent.Refresh)
    }

    Scaffold(
        floatingActionButton = {
            if (state is MainState.Data) {
                FloatingActionButton(
                    onClick = { navController.navigate(BookScreenDestination) },
                    modifier = Modifier.testTag(TestIds.Main.ADD_BUTTON)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is MainState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is MainState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error",
                            modifier = Modifier.testTag(TestIds.Main.ERROR)
                        )
                        Button(
                            onClick = { viewModel.onIntent(MainIntent.Refresh) },
                            modifier = Modifier.testTag(TestIds.Main.REFRESH_BUTTON)
                        ) {
                            Text("Refresh")
                        }
                    }
                }
                is MainState.Data -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (currentState.avatar != null) {
                                    AsyncImage(
                                        model = currentState.avatar,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .testTag(TestIds.Main.PROFILE_IMAGE),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                     // Placeholder if no image
                                     Box(
                                         modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .testTag(TestIds.Main.PROFILE_IMAGE)
                                     )
                                }
                                Spacer(modifier = Modifier.size(16.dp))
                                Text(
                                    text = currentState.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.testTag(TestIds.Main.PROFILE_NAME)
                                )
                            }
                            Button(
                                onClick = { viewModel.onIntent(MainIntent.Logout) },
                                modifier = Modifier.testTag(TestIds.Main.LOGOUT_BUTTON)
                            ) {
                                Text("Logout")
                            }
                        }
                        
                        Button(
                            onClick = { viewModel.onIntent(MainIntent.Refresh) },
                            modifier = Modifier
                                .testTag(TestIds.Main.REFRESH_BUTTON)
                                .padding(vertical = 8.dp)
                        ) {
                            Text("Refresh")
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(currentState.bookings) { index, booking ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag(TestIds.Main.getIdItemByPosition(index))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = booking.date,
                                        modifier = Modifier.testTag(TestIds.Main.ITEM_DATE)
                                    )
                                    Text(
                                        text = booking.place,
                                        modifier = Modifier.testTag(TestIds.Main.ITEM_PLACE)
                                    )
                                }
                            }
                        }
                        
                        // Add hidden error field as per requirements
                        // "По умолчанию скрытое текстовое поле с ошибкой (main_error)."
                        // It should only show on error, but MainState.Error handles full screen error.
                        // However, if there is a partial error or just hidden field requirement?
                        // Specs say: "В случае любой ошибки необходимо скрыть все элементы, кроме текстового поля с ошибкой и кнопки обновления данных."
                        // This is handled in MainState.Error branch.
                        
                    }
                }
            }
        }
    }
}
