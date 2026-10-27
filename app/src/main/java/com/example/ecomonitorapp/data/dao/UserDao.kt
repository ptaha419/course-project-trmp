package com.example.ecomonitorapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.ecomonitorapp.models.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE id IN (:userIds)")
    suspend fun loadAllByIds(userIds: IntArray): List<User>

    @Query(
        """
        SELECT * FROM user
        WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1
        """
    )
    suspend fun findByName(first: String, last: String): User

    @Insert
    suspend fun insertAll(vararg users: User)

    @Delete
    suspend fun delete(user: User)

    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM user WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT * FROM user WHERE email = :email")
    suspend fun getByEmail(email: String): User?
}