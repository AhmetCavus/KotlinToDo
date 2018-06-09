package com.ams.cavus.todo.db

import com.ams.cavus.todo.db.entity.CredentialsData
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

typealias fetchAccountSuccess = (credentials: CredentialsData) -> Unit
typealias fetchAccountFail = (error: Exception) -> Unit

class DbService(val appDb: AppDb) {

    fun fetchLastAccount(success: fetchAccountSuccess, fail: fetchAccountFail){
        doAsync {
            val lastAccount =  appDb.credentialsDataDao().selectLastAccount()
            uiThread {
                if(lastAccount != null) success.invoke(lastAccount)
                else fail.invoke(Exception("No valid account found"))
            }
        }
    }
}