package com.example.note.di

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Room
import com.example.note.Converters
import com.example.note.localData.LocalDataSource
import com.example.note.localData.LocalDataSourceI
import com.example.note.noteModels.AppDatabase
import com.example.note.repository.Repository
import com.example.note.repository.RepositoryI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room
            .databaseBuilder(appContext, AppDatabase::class.java, "database-name")
            .build()
    }

    @Provides
    fun provideRepository(localDataSource: LocalDataSourceI): RepositoryI = Repository(localDataSource)

    @Provides
    fun provideLocalDataSource(appDatabase: AppDatabase): LocalDataSourceI = LocalDataSource(appDatabase)

}