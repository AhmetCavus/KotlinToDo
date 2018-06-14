package com.ams.cavus.todo.list.model

import java.util.*

/**
 * Represents an item in a ToDo list
 */
data class Todo(
        @com.google.gson.annotations.SerializedName("userId")
        var userId: String,
        @com.google.gson.annotations.SerializedName("text")
        var text: String) {

    @com.google.gson.annotations.SerializedName("id")
    var id: String = System.currentTimeMillis().toString()

    @com.google.gson.annotations.SerializedName("createdAt")
    var createdAt = Date()

    @com.google.gson.annotations.SerializedName("completed")
    var completed: Boolean = false

    @com.google.gson.annotations.SerializedName("deleted")
    var deleted: Boolean = false

    override fun toString() = text

    override fun equals(other: Any?): Boolean {
        return other is Todo && other.id === id
    }

}