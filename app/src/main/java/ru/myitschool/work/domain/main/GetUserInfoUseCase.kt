package ru.myitschool.work.domain.main

import ru.myitschool.work.data.model.UserInfo
import ru.myitschool.work.data.repo.MainRepository

class GetUserInfoUseCase(
    private val repository: MainRepository
) {
    suspend operator fun invoke(): Result<UserInfo> {
        return repository.getUserInfo()
    }
}