package com.example.workerdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(context: Context,workerParams: WorkerParameters):Worker(context, workerParams) {
    override fun doWork(): Result {
        val key = inputData.getInt("key",0)
        showNotification("Workmanager", "Nachricht gesendet mit key $key")
        val outputData = Data.Builder().putString("result", "Job finished").build()
        return Result.success(outputData)
    }

    private fun showNotification(title:String, text:String){
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "id"
        val channelName = "Name"

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(applicationContext,channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_background)
        manager.notify(1,builder.build())
    }
}