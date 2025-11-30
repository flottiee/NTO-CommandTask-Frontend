package ru.myitschool.work.ui.screen.main

sealed interface MainIntent {
    object Refresh : MainIntent
    object Logout : MainIntent
}