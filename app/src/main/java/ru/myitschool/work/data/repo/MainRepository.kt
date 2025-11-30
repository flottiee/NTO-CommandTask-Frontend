package ru.myitschool.work.data.repo

import ru.myitschool.work.data.model.UserInfo
import ru.myitschool.work.data.source.NetworkDataSource

object MainRepository {
    suspend fun getUserInfo(): Result<UserInfo> {
        val code = AuthRepository.getAuthCode() ?: return Result.failure(Exception("No auth code"))
        return NetworkDataSource.getUserInfo(code)
    }

    suspend fun logout() {
        AuthRepository.clearAuth()
    }
}