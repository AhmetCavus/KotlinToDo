package com.ams.cavus.todo.client

import com.ams.cavus.todo.helper.Settings
import com.google.gson.Gson
import com.microsoft.windowsazure.mobileservices.MobileServiceClient

abstract class AzureService(protected val client: MobileServiceClient, private val gson: Gson, private var settings: Settings)