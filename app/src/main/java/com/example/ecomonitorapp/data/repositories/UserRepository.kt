package com.example.ecomonitorapp.data.repositories

import android.content.Context
import com.example.ecomonitorapp.data.dao.UserDao
import com.example.ecomonitorapp.data.db.DbProvider
import com.example.ecomonitorapp.data.entities.UserEntity
import com.example.ecomonitorapp.models.UserRole

class UserRepository(private val context: Context) {
    // получаем DAO
    private val dao: UserDao = DbProvider.getDatabase(context).userDao()

    // Регистрация пользователя
    suspend fun registerUser(email: String,
                             firstName: String,
                             middleName: String?,
                             lastName: String,
                             phoneNumber: String,
                             passport: String,
                             passwordHash: String?
                             // на случай, если регистрация всегда как заявителя
                             // админ отдельно регистрирует другие роли
                             //role: UserRole = UserRole.APPLICANT
    ) : Boolean {
        return try {
            val userEntity = UserEntity(
                email = email,
                firstName = firstName,
                middleName = middleName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                passport = passport,
                passwordHash = passwordHash,
                role = UserRole.APPLICANT.name)
            dao.insert(userEntity)
            true
        } catch(e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Авторизация пользователя
    suspend fun loginUser(email: String): UserEntity? = dao.getByEmail(email)
}