package com.ams.cavus.todo.base.di

import android.arch.persistence.room.Room
import com.ams.cavus.client.AzureApi
import com.ams.cavus.todo.R
import com.ams.cavus.todo.base.App
import com.ams.cavus.todo.db.AppDb
import com.ams.cavus.todo.db.DbService
import com.ams.cavus.todo.helper.Settings
import com.ams.cavus.todo.list.service.IdentityService
import com.ams.cavus.todo.login.service.AzureAuthService
import com.google.gson.Gson
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import com.squareup.okhttp.OkHttpClient
import dagger.Module
import dagger.Provides
import java.util.concurrent.TimeUnit
import javax.inject.Named
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
    fun provideAppDb(app: App) =
            Room.databaseBuilder(
                    app.applicationContext,
                    AppDb::class.java,
                    "amstodo.db"
            )
                    .fallbackToDestructiveMigration()
                    .build()
    @Singleton
    @Provides
    fun provideDbService(appDb: AppDb) = DbService(appDb)

    @Singleton
    @Provides
    fun providePreferences(app: App) = com.ams.cavus.todo.helper.Settings(app.baseContext)

    @Singleton
    @Provides
    fun provideGson() = Gson()

    @Named("azureScope")
    @Provides
    fun provideAzureScope(app: App): String = app.getString(R.string.azure_scope)

    @Singleton
    @Provides
    fun provideAuthService(client: MobileServiceClient, gson: Gson, settings: Settings) = AzureAuthService(client, gson, settings)

    @Singleton
    @Provides
    fun provideIdentityService(client: MobileServiceClient, gson: Gson, settings: Settings) = IdentityService(client, gson, settings)

}