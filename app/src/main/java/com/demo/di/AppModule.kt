package com.demo.di

import android.app.Application
import androidx.room.Room
import com.demo.repository.local.dao.UserDataDao
import com.demo.repository.local.database.UserDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideDb(app: Application): UserDatabase {
        return Room
            .databaseBuilder(app, UserDatabase::class.java, "user.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesUserDao(db: UserDatabase): UserDataDao {
        return db.userDataDao()
    }

}