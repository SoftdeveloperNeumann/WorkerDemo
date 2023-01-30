package com.example.workerdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.workerdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        workManager = WorkManager.getInstance(this)
        binding.btnNotification.setOnClickListener {
//            showNotification()
            val data = Data.Builder()
                .putInt("key", 123)
                .build()
           val oneTimeRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
               .setInputData(data)
               .build()
            workManager.enqueue(oneTimeRequest)
            
            workManager.getWorkInfoByIdLiveData(oneTimeRequest.id).observe(this,{ info : WorkInfo ->
                if(info.state.isFinished){
                    val result = info.outputData.getString("result")
                    Toast.makeText(this, "Inhalt: $result", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showNotification(){
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "id"
        val channelName = "Name"

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(applicationContext,channelId)
            .setContentTitle("Mitteilung")
            .setContentText("Der wichtige Inhalt")
            .setSmallIcon(R.drawable.ic_launcher_background)
        manager.notify(1,builder.build())
    }
}