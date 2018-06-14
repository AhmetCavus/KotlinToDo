package com.ams.cavus.todo.list.model

import java.util.*

/**
 * Represents an item in a User list
 */
data class Identity(
        @com.google.gson.annotations.SerializedName("userid")
        var userId: String,
        @com.google.gson.annotations.SerializedName("username")
        var userName: String) {

    @com.google.gson.annotations.SerializedName("id")
    var id: String = System.currentTimeMillis().toString()

    @com.google.gson.annotations.SerializedName("createdAt")
    var createdAt = Date()

    @com.google.gson.annotations.SerializedName("deleted")
    var deleted: Boolean = false

    override fun toString() = "$userName/$userId"

    override fun equals(other: Any?): Boolean {
        return other is Identity && other.id === id
    }

}