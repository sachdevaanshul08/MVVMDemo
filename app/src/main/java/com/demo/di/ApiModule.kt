package com.demo.di

import android.app.Application
import com.demo.BuildConfig
import com.demo.data.network.api.DeliveryApi
import com.demo.util.networkradapter.ApiResponseAdapterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class ApiModule {

    /*
     * The method returns the Gson object
     * */
    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }


    /*
     * The method returns the Cache object
     * */
    @Provides
    @Singleton
    internal fun provideCache(application: Application): Cache {
        val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB
        val httpCacheDirectory = File(application.cacheDir, "http-cache")
        return Cache(httpCacheDirectory, cacheSize)
    }


    /*
     * The method returns the Okhttp object
     * */
    @Provides
    @Singleton
    internal fun provideOkhttpClient(cache: Cache): OkHttpClient {

        val httpClient = OkHttpClient.Builder()
        httpClient.cache(cache)
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(logging)
        }
        //Following values can be overrideen by the apis response if there is any change in the timeout.
        //That logic can be added here.
        httpClient.connectTimeout(BuildConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        httpClient.readTimeout(BuildConfig.READ_TIMEOUT, TimeUnit.SECONDS)
        return httpClient.build()
    }


    /*
     * The method returns the Retrofit object
     * */
    @Provides
    @Singleton
    internal fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(ApiResponseAdapterFactory())
            .baseUrl(BuildConfig.BASEURL)
            .client(okHttpClient)
            .build()
    }


    /*
     *
     *to get the deliveryApi we need retrofit, okHttpClient, cache and Gson object.
     * so all has been injected via dagger2
     * */
    @Provides
    @Singleton
    internal fun provideUserApi(retrofit: Retrofit): DeliveryApi {
        return retrofit.create(DeliveryApi::class.java)
    }
}