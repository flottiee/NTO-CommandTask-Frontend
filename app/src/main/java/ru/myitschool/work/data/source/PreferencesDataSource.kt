package ru.myitschool.work.data.source

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesDataSource(
    private val context: Context
) {
    val Context.myDataStore by preferencesDataStore(name = "user_prefs")

    private val AUTH_CODE_KEY = stringPreferencesKey("auth_code")

    val authCode: Flow<String?> = context.myDataStore.data
        .map { preferences ->
            preferences[AUTH_CODE_KEY]
        }

    suspend fun saveAuthCode(code: String) {
        context.myDataStore.edit { preferences ->
            preferences[AUTH_CODE_KEY] = code
        }
    }

    suspend fun clearAuthCode() {
        context.myDataStore.edit { preferences ->
            preferences.remove(AUTH_CODE_KEY)
        }
    }
}