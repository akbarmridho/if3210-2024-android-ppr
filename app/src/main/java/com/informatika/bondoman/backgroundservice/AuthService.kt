package com.informatika.bondoman.backgroundservice

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.repository.connectivity.ConnectivityRepository
import com.informatika.bondoman.model.repository.token.TokenRepository
import com.informatika.bondoman.prefdatastore.jwt.JWTManager
import com.informatika.bondoman.view.activity.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

class AuthService : Service() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var timer: Timer? = null
    private val jwtManager: JWTManager by inject()
    private val tokenRepository: TokenRepository by inject()
    private val connectivityRepository: ConnectivityRepository by inject()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        performLongTask()
        return START_STICKY
    }

    private fun performLongTask() {
        if (timer != null) {
            return
        }

        timer = timer(
            "auth-check",
            initialDelay = TimeUnit.MINUTES.toMillis(1),
            period = TimeUnit.MINUTES.toMillis(1)
        ) {
            scope.launch {
                Timber.tag("JWT").d("IM RUNNNINGGGGGG WOHO")
                val expire = isExpired()
                Timber.tag("JWT").d("got result $expire")

                if (expire) {
                    jwtManager.onLogout()

                    val intent = Intent(applicationContext, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)

                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post {
                        Toast.makeText(applicationContext, "Token expired", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private suspend fun isExpired(): Boolean {
        val isConnectedLiveData = connectivityRepository.lastStatus()

        return try {
            if (isConnectedLiveData == null || !isConnectedLiveData) {
                return false
            }

            when (val result = tokenRepository.token(jwtManager.getToken())) {
                is Resource.Success -> {
                    return !result.data
                }

                else -> {
                    jwtManager.onLogout()
                    return true
                }
            }
        } catch (e: Exception) {
            Timber.tag("JWT").d("No token found")
            return false // false here because we assume if no token was found, user already in login activity
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer = null
        job.cancel()
    }
}