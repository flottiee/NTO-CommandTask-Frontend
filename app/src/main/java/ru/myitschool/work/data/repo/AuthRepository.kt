package ru.myitschool.work.data.repo

import ru.myitschool.work.data.source.NetworkDataSource
import ru.myitschool.work.data.source.PreferencesDataSource

object AuthRepository {

    suspend fun checkAndSave(text: String): Result<Boolean> {
        return NetworkDataSource.checkAuth(text).onSuccess { success ->
            if (success) {
                PreferencesDataSource.saveCode(text)
            }
        }
    }
}