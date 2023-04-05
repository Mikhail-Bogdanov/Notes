package com.example.note

import android.app.Application
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import com.example.note.fragments.MainFragment
import com.example.note.noteModels.AppDatabase
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application()