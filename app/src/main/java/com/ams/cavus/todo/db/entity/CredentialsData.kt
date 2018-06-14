package com.ams.cavus.todo.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.ams.cavus.todo.client.AzureCredentials

@Entity
data class CredentialsData(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @ColumnInfo(name = "authenticationToken") var authToken: String,
        @ColumnInfo(name = "userId") var userId: String,
        @ColumnInfo(name = "userName") var email: String,
        @ColumnInfo(name = "provider") var provider: String,
        @ColumnInfo(name = "access_token") var accessToken: String,
        @ColumnInfo(name = "id_token") var idToken: String) {
    fun isValid() = authToken.isNotEmpty() && userId.isNotEmpty()
    fun toAzure() = AzureCredentials(accessToken, idToken, provider)
}