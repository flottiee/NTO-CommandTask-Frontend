package ru.myitschool.work.data.source

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

class PreferencesDataSource(
    private val context: Context
) {
    val Context.myDataStore by preferencesDataStore(name = "user_prefs")
}