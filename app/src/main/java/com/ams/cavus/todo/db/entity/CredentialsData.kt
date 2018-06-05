package com.ams.cavus.todo.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
data class CredentialsData(
        @PrimaryKey(autoGenerate = true) @Expose val id: Long,
        @ColumnInfo(name = "authenticationToken") @Expose var authToken: String,
        @ColumnInfo(name = "userId") @Expose var userId: String,
        @ColumnInfo(name = "email") @Expose var email: String,
        @ColumnInfo(name = "access_token") @SerializedName("access_token") var accessToken: String,
        @ColumnInfo(name = "id_token") @SerializedName("id_token") var idToken: String
)