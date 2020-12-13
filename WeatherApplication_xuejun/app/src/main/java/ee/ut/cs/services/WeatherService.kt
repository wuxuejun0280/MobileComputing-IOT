package ee.ut.cs.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class WeatherService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}