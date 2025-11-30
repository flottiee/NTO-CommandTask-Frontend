package ru.myitschool.work.ui.screen.book

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.myitschool.work.core.TestIds

@Composable
fun BookScreen(
    navController: NavController,
    viewModel: BookViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.actionFlow.collect {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.testTag(TestIds.Book.BACK_BUTTON)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is BookState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is BookState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error",
                            modifier = Modifier.testTag(TestIds.Book.ERROR)
                        )
                        Button(
                            onClick = { viewModel.onIntent(BookIntent.Refresh) },
                            modifier = Modifier.testTag(TestIds.Book.REFRESH_BUTTON)
                        ) {
                            Text("Refresh")
                        }
                    }
                }
                is BookState.Empty -> {
                    Text(
                        text = "Всё забронировано",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag(TestIds.Book.EMPTY)
                    )
                }
                is BookState.Data -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        PrimaryScrollableTabRow(
                            selectedTabIndex = currentState.selectedDateIndex,
                            edgePadding = 0.dp
                        ) {
                            currentState.availableDays.forEachIndexed { index, day ->
                                Tab(
                                    selected = currentState.selectedDateIndex == index,
                                    onClick = { viewModel.onIntent(BookIntent.SelectDate(index)) },
                                    modifier = Modifier.testTag(TestIds.Book.getIdDateItemByPosition(index))
                                ) {
                                    Text(
                                        text = formatDate(day.date),
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .testTag(TestIds.Book.ITEM_DATE)
                                    )
                                }
                            }
                        }

                        val currentDay = currentState.availableDays.getOrNull(currentState.selectedDateIndex)
                        if (currentDay != null) {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                itemsIndexed(currentDay.bookings) { index, booking ->
                                    val isSelected = currentState.selectedPlace == booking.id
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag(TestIds.Book.getIdPlaceItemByPosition(index))
                                            .selectable(
                                                selected = isSelected,
                                                onClick = { viewModel.onIntent(BookIntent.SelectPlace(booking.id)) }
                                            )
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = booking.place,
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag(TestIds.Book.ITEM_PLACE_TEXT)
                                        )
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = null, // handled by selectable
                                            modifier = Modifier.testTag(TestIds.Book.ITEM_PLACE_SELECTOR)
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = { viewModel.onIntent(BookIntent.Book) },
                                enabled = currentState.selectedPlace != null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .testTag(TestIds.Book.BOOK_BUTTON)
                            ) {
                                Text("Book")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    // Assuming dateString is YYYY-MM-DD or dd.MM.yyyy from API? 
    // The MainScreen required parsing dd.MM.yyyy. 
    // If models are shared, let's assume standard API returns YYYY-MM-DD usually, or dd.MM.yyyy.
    // If the parser in MainViewModel worked, then it was dd.MM.yyyy.
    // Let's be robust.
    
    return try {
        if (dateString.contains(".")) {
            val parts = dateString.split(".")
             // dd.MM.yyyy -> dd.MM
             if (parts.size >= 2) {
                 "${parts[0]}.${parts[1]}"
             } else {
                 dateString
             }
        } else if (dateString.contains("-")) {
             val parts = dateString.split("-")
             // yyyy-MM-dd -> dd.MM
             if (parts.size == 3) {
                 "${parts[2]}.${parts[1]}"
             } else {
                 dateString
             }
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}
