package com.ams.cavus.todo.list.model

/**
 * Represents an item in a ToDo list
 */
data class ToDoItem(
        @com.google.gson.annotations.SerializedName("text")
        var text: String,

        @com.google.gson.annotations.SerializedName("id")
        var id: String) {

    /**
     * Indicates if the item is completed
     */
    /**
     * Indicates if the item is marked as completed
     */
    /**
     * Marks the item as completed or incompleted
     */
    @com.google.gson.annotations.SerializedName("complete")
    var isComplete: Boolean = false

    init {
        this.text = text
        this.id = id
    }

    override fun toString(): String {
        return text
    }

    override fun equals(o: Any?): Boolean {
        return o is ToDoItem && o.id === id
    }
}