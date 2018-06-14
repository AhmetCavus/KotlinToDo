package com.ams.cavus.todo.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.ams.cavus.todo.db.entity.CredentialsData

@Dao
interface CredentialsDataDao {

    @Query("SELECT * from CredentialsData")
    fun getAll(): List<CredentialsData>?

    @Query("SELECT * from CredentialsData where userName = :userName")
    fun selectAccount(userName: String): CredentialsData?

    @Query("SELECT * from CredentialsData limit 1")
    fun selectLastAccount(): CredentialsData?

    @Insert(onConflict = REPLACE)
    fun insert(credentialsData: CredentialsData)

}