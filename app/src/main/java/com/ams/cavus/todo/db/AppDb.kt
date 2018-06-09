package com.ams.cavus.todo.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.ams.cavus.todo.db.dao.CredentialsDataDao
import com.ams.cavus.todo.db.entity.CredentialsData

@Database(entities = arrayOf(CredentialsData::class), version = 3)
abstract class AppDb : RoomDatabase() {
    abstract fun credentialsDataDao(): CredentialsDataDao
}