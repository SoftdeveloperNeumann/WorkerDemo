package com.example.workerdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.workerdemo.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

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
            
            workManager.getWorkInfoByIdLiveData(oneTimeRequest.id).observe(this) { info: WorkInfo ->
                if (info.state.isFinished) {
                    val result = info.outputData.getString("result")
                    Toast.makeText(this, "Inhalt: $result", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.btnPeriodic.setOnClickListener {
            val periodicRequest = PeriodicWorkRequestBuilder<NotificationWorker>(PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                TimeUnit.MILLISECONDS).build()
            workManager.enqueue(periodicRequest)
            workManager.getWorkInfoByIdLiveData(periodicRequest.id).observe(this){info ->
                if(info != null){
                    binding.textView.append("\n${info.state.name}")

                }
//                if(info.state.name.equals("ENQUEUED")){
//                    workManager.cancelWorkById(periodicRequest.id)
//                }
            }
        }
        binding.btnConstraints.setOnClickListener {
            val constrains = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val oneTimeRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setConstraints(constrains)
                .build()
            workManager.enqueue(oneTimeRequest)

            workManager.getWorkInfoByIdLiveData(oneTimeRequest.id).observe(this){
                if(it != null){
                    binding.textView.text = "${it.state}"
                }
            }
        }

        binding.btnSequence.setOnClickListener {
            val download = OneTimeWorkRequestBuilder<DownloadWorker>().build()
            val filter = OneTimeWorkRequestBuilder<FilterWorker>().build()
            val compress = OneTimeWorkRequestBuilder<CompressWorker>().build()
            val upload = OneTimeWorkRequestBuilder<UploadWorker>().build()

            val parallel = mutableListOf<OneTimeWorkRequest>()
            parallel.add(filter)
            parallel.add(compress)

            workManager.beginWith(download)
                .then(parallel)
                .then(upload)
                .enqueue()
            workManager.getWorkInfoByIdLiveData(upload.id).observe(this){
                if(it.state.isFinished){
                    binding.textView.text = it.outputData.getString("key")
                }
            }
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