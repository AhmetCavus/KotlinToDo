package com.ams.cavus.todo.client

data class AzureCredentials(val access_token: String, val id_token: String, val email: String, val provider: String) {
    lateinit var authToken: String
    lateinit var userId: String
}