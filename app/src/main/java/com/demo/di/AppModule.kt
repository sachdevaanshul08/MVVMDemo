package com.demo.di

import android.app.Application
import androidx.room.Room
import com.demo.BuildConfig
import com.demo.repository.local.dao.DeliveryDao
import com.demo.repository.local.database.DeliveryDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideDb(app: Application): DeliveryDatabase {
        return Room
            .databaseBuilder(app, DeliveryDatabase::class.java, BuildConfig.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesDeliveryDao(db: DeliveryDatabase): DeliveryDao {
        return db.getDeliveryDao()
    }

}