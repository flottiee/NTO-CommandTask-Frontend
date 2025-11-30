package ru.myitschool.work.data.repo

import kotlinx.coroutines.flow.first
import ru.myitschool.work.App
import ru.myitschool.work.data.source.NetworkDataSource

object AuthRepository {

    private val preferencesDataSource = PreferencesDataSource(App.context)

    suspend fun checkAndSave(text: String): Result<Boolean> {
        return NetworkDataSource.checkAuth(text).onSuccess { success ->
            if (success) {
                preferencesDataSource.saveAuthCode(text)
            }
        }
    }

    suspend fun getAuthCode(): String? {
        return preferencesDataSource.authCode.first()
    }

    suspend fun clearAuth() {
        preferencesDataSource.clearAuthCode()
    }
}