package com.example.note.workManager

import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import kotlinx.coroutines.delay

class MyWorkManager(context: Context, parameters: WorkerParameters)
    : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {

        Log.d("auf", "some process")

        val valueA = inputData.getString("keyA")
        val valueB = inputData.getInt("keyB", 0)

        Log.d("auf", valueA!!)
        Log.d("auf", valueB.toString())

        return Result.success()
    }

}

