package com.example.ecomonitorapp.data.repositories

import android.content.Context
import com.example.ecomonitorapp.data.dao.UserDao
import com.example.ecomonitorapp.data.db.AppDatabase
import com.example.ecomonitorapp.data.db.DbProvider
import com.example.ecomonitorapp.data.entities.UserEntity
import com.example.ecomonitorapp.models.UserRole

class UserRepository(private val context: Context) {
    private val dao: UserDao = DbProvider.getDatabase(context).userDao()

    suspend fun registerUser(
        email: String,
        firstName: String,
        middleName: String?,
        lastName: String,
        phoneNumber: String,
        passport: String,
        passwordHash: String?
    ): UserEntity? {
        return try {
            val userEntity = UserEntity(
                id = null, // Room сам заполнит это
                email = email,
                firstName = firstName,
                middleName = middleName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                passport = passport,
                passwordHash = passwordHash,
                role = UserRole.APPLICANT.name
            )

            // получаем id, который сгенерировала БД
            val newId = dao.insert(userEntity)

            // делаем SELECT, чтобы получить объект с реальным id из БД
            dao.getByEmail(email)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Авторизация пользователя
    suspend fun loginUser(email: String): UserEntity? = dao.getByEmail(email)

    suspend fun getUserById(id: Long): UserEntity? = dao.getById(id)

    suspend fun getUserRole(userId: Long): UserRole? {
        if (userId == -1L) return null // никто не залогинен

        return dao.getById(userId)?.let { entity ->
                UserRole.valueOf(entity.role)
            }
    }
    suspend fun updateUser(user: UserEntity): Boolean = try {
        dao.update(user)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    fun saveCurrentUserId(id: Long) {
        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .edit().putLong("current_user_id", id).apply()
    }

    fun getCurrentUserId(): Long =
        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getLong("current_user_id", -1L)
}