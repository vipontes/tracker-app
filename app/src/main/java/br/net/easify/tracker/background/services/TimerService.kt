package br.net.easify.tracker.background.services

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.net.easify.tracker.helpers.Constants
import java.util.*

class TimerService : Service() {

    private var elapsedTime: Long = 0

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    companion object {
        val timerServiceElapsedTimeChanged = "br.net.easify.tracker.background.services.TimerService"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startTimer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimerTask()
    }

    fun startTimer() {
        timer = Timer()
        initializeTimerTask()
        timer!!.schedule(timerTask, 1000, 1000)
    }

    fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
                elapsedTime += 1000

                val intent = Intent(timerServiceElapsedTimeChanged)
                intent.putExtra(Constants.resultCode, Activity.RESULT_OK)
                intent.putExtra(Constants.elapsedTime, elapsedTime)

                val broadcastManager = LocalBroadcastManager.getInstance(applicationContext)
                broadcastManager.sendBroadcast(intent)
            }
        }
    }

    fun stopTimerTask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

}