package ru.myitschool.work.ui.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import ru.myitschool.work.data.source.PreferencesDataSource
import ru.myitschool.work.ui.nav.AuthScreenDestination
import ru.myitschool.work.ui.nav.BookScreenDestination
import ru.myitschool.work.ui.nav.MainScreenDestination
import ru.myitschool.work.ui.screen.auth.AuthScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val startDest = runBlocking {
        if (PreferencesDataSource.userCodeFlow.first()
                .isEmpty()
        ) AuthScreenDestination else MainScreenDestination
    }
    NavHost(
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        navController = navController,
        startDestination = startDest,
    ) {
        composable<AuthScreenDestination> {
            AuthScreen(navController = navController)
        }
        composable<MainScreenDestination> {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Hello")
            }
        }
        composable<BookScreenDestination> {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Hello")
            }
        }
    }
}