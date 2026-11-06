package com.example.ecomonitorapp.data.mapper

import com.example.ecomonitorapp.data.entities.UserEntity
import com.example.ecomonitorapp.models.User
import com.example.ecomonitorapp.models.UserRole

// Преобразование объектов классов UserEntity и User
// Маппинг реализуется функциями расширения toUser() и toEntity()

fun UserEntity.toUser(): User = User(
    id = id ?: -1, // страховка при создании пользователя
    email = email,
    firstName = firstName,
    middleName = middleName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    passport = passport,
    role = try {
        UserRole.valueOf(role)
    } catch(e: IllegalArgumentException) {
        UserRole.APPLICANT
    }
)

fun User.toEntity(existing: UserEntity? = null): UserEntity = UserEntity(
    id = existing?.id ?: id, // чтобы при update не потерять реальный id
    email = email,
    firstName = firstName,
    middleName = middleName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    passport = passport,
    passwordHash = existing?.passwordHash, // пароль нужно менять отдельно
    role = role.name
)