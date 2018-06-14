package com.ams.cavus.todo.client

data class AzureCredentials(var access_token: String, var id_token: String, var provider: String) {
    var authToken = ""
    var userId = ""
    var userName = ""
}

fun emptyCredentials() = AzureCredentials("", "", "")