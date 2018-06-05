package com.ams.cavus.todo.base.di

import android.arch.persistence.room.Room
import com.ams.cavus.client.AzureApi
import com.ams.cavus.todo.base.App
import com.ams.cavus.todo.db.AppDb
import com.google.gson.Gson
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import com.squareup.okhttp.OkHttpClient
import dagger.Module
import dagger.Provides
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule(val app: App) {

    @Singleton
    @Provides
    fun provideApp() = app

    @Singleton
    @Provides
    fun provideAzureClient(app: App): MobileServiceClient {
        // Mobile Service URL and key
        return MobileServiceClient(AzureApi.BASE_URL, app).apply {
            // Extend timeout from default of 10s to 20s
            setAndroidHttpClientFactory(
                    {
                        val client = OkHttpClient()
                        client.setReadTimeout(20, TimeUnit.SECONDS)
                        client.setWriteTimeout(20, TimeUnit.SECONDS)
                        client
                    })
        }
    }

    @Singleton
    @Provides
    fun provideGson() = Gson()

    @Singleton
    @Provides
    fun provideAppDb(app: App) =
            Room.databaseBuilder(
                app.applicationContext,
                AppDb::class.java,
                "amstodo.db"
            )
            .fallbackToDestructiveMigration()
            .build()

//    @Singleton
//    @Provides
//    fun provideMobileServiceTable(client: MobileServiceClient): MobileServiceTable<ToDoItem>
//            =  client.getTable(ToDoItem::class.java)
}