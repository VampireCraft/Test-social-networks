package ru.bikbulatov.comeWithMe

import android.R
import android.app.Application
import android.content.SharedPreferences
import android.widget.Toast
import com.chibatching.kotpref.Kotpref
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import ru.bikbulatov.comeWithMe.core.network.TokenRepository


@HiltAndroidApp
class BaseApp : Application() {
    companion object {
        lateinit var instance: BaseApp
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Kotpref.init(this)
        sharedPreferences = getSharedPreferences("default", MODE_PRIVATE)
        TokenRepository.loadTokenFromShared(
            sharedPreferences.getString("accessToken", "") ?: "",
            sharedPreferences.getString("refreshToken", "") ?: ""
        )


        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                MainPrefs.firebaseTokenIsSend = false
                val token = task.result
                MainPrefs.firebaseToken = token
            })
    }
}