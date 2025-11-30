package ru.myitschool.work.data.source

import android.content.Context

class PreferencesDataSource(
    private val context: Context
) {
    val Context.myDataStore by preferencesDataStore(name = "user_prefs")
}