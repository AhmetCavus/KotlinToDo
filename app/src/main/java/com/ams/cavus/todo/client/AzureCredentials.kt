package com.ams.cavus.todo.client

data class AzureCredentials(var access_token: String, var id_token: String, var email: String, var provider: String) {
    lateinit var authToken: String
    lateinit var userId: String
}

fun emptyCredentials() = AzureCredentials("", "", "", "")