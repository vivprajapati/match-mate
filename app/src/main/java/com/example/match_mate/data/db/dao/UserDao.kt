package com.example.match_mate.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.match_mate.data.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    suspend fun insertUser(user: User) {
        insertUsers(listOf(user))
    }

    @Query("SELECT * FROM users WHERE status = 'self'")
    suspend fun getLoggedInUser(): User?

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE status = 'accepted'")
    fun getAcceptedUsers(): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE status = 'declined'")
    fun getDeclinedUsers(): LiveData<List<User>>

    @Query("UPDATE users SET status = 'accepted' WHERE uuid = :uuid")
    suspend fun markUserAsAccepted(uuid: String)

    @Query("UPDATE users SET status = 'declined' WHERE uuid = :uuid")
    suspend fun markUserAsDeclined(uuid: String)

    @Query("DELETE FROM users")
    suspend fun clearAll()
}