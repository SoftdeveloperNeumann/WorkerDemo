package com.example.workerdemo

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

val TAG = "DataWorker"

class DownloadWorker(context: Context, params:WorkerParameters) : Worker(context,params){

    override fun doWork(): Result {
        try{
            for (i in 0..30){
                Thread.sleep(500)
                Log.d(TAG, "Download:  $i")
            }
            return Result.success()

        }catch(e:Exception){
            return Result.failure()
        }
    }
}
class FilterWorker(context: Context, params:WorkerParameters) : Worker(context,params){

    override fun doWork(): Result {
        try{
            for (i in 0..30){
                Thread.sleep(300)
                Log.d(TAG, "Filter:  $i")
            }
            return Result.success()

        }catch(e:Exception){
            return Result.failure()
        }
    }
}
class CompressWorker(context: Context, params:WorkerParameters) : Worker(context,params){


    override fun doWork(): Result {
        try{
            for (i in 0..30){
                Thread.sleep(500)
                Log.d(TAG, "Compress:  $i")
            }
            return Result.success()

        }catch(e:Exception){
            return Result.failure()
        }
    }
}
class UploadWorker(context: Context, params:WorkerParameters) : Worker(context,params){


    override fun doWork(): Result {
        try{
            for (i in 0..30){
                Thread.sleep(200)
                Log.d(TAG, "Upload:  $i")
            }
            return Result.success()

        }catch(e:Exception){
            return Result.failure()
        }
    }
}
