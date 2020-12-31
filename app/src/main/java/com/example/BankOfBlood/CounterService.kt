package com.example.BankOfBlood

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class CounterService : Service() {

    val TAG = "CounterService"
    override fun onBind(intent: Intent): IBinder? {
        return null

    }

    fun showLog(message: String)
    {
        Log.d(TAG, "showLog: Counter Service is running")
    }

    override fun onCreate() {
        showLog("onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        for(i in 1..1000)
        {
            showLog("inside onStartCommand, running for the" + (i.toString()) + " th time")
            //Thread.sleep(1000)
        }
        return START_STICKY
    }
}
