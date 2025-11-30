package ru.myitschool.work.data.source

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.myitschool.work.App

object PreferencesDataSource {
    val Context.myDataStore by preferencesDataStore(name = "user_prefs")
    private val userCodeKey = stringPreferencesKey("USER_CODE")

    val userCodeFlow: Flow<String> = App.context.myDataStore.data.map { it[userCodeKey] ?: "" }

    suspend fun saveCode(code: String) {
        App.context.myDataStore.edit {
            it[userCodeKey] = code
        }
    }
}