package ru.bikbulatov.comeWithMe.core.network

import android.util.Base64
import org.json.JSONObject
import ru.bikbulatov.comeWithMe.BaseApp
import java.util.*

object TokenRepository {
    var accessToken: String = ""
    var refreshToken: String = ""
    var accessTokenExpireTime: Long = 0L
    var refreshTokenExpireTime: Long = 0L


    fun isRefreshTokenValid(): Boolean {
        if (refreshToken == null || refreshToken.isEmpty())
            return false
        return refreshTokenExpireTime - System.currentTimeMillis() > 0
    }

    fun saveTokens(_accessToken: String, _refreshToken: String) {
        setTokensToLocal(_accessToken, _refreshToken)
        saveTokensToSharedPref(_accessToken, _refreshToken)
    }

    private fun setTokensToLocal(_accessToken: String, _refreshToken: String) {
        accessToken = _accessToken
        refreshToken = _refreshToken
        accessTokenExpireTime = getExpiredTimeFromMap(decodeJwtToken(_accessToken))
        refreshTokenExpireTime = getExpiredTimeFromMap(decodeJwtToken(_refreshToken))
    }

    private fun saveTokensToSharedPref(_accessToken: String, _refreshToken: String) {
        BaseApp.sharedPreferences.edit().putString("accessToken", _accessToken).apply()
        BaseApp.sharedPreferences.edit()
            .putLong("accessTokenLiveTime", getExpiredTimeFromMap(decodeJwtToken(accessToken)))
            .apply()
        BaseApp.sharedPreferences.edit().putString("refreshToken", _refreshToken).apply()
        BaseApp.sharedPreferences.edit()
            .putLong("refreshTokenLiveTime", getExpiredTimeFromMap(decodeJwtToken(refreshToken)))
            .apply()
    }

    fun loadTokenFromShared(_accessToken: String, _refreshToken: String) {
        setTokensToLocal(_accessToken, _refreshToken)
    }

    fun deleteTokens() {
        accessToken = ""
        refreshToken = ""
        accessTokenExpireTime = 0L
        refreshTokenExpireTime = 0L
        BaseApp.sharedPreferences.edit().putString("accessToken", "").apply()
        BaseApp.sharedPreferences.edit().putLong("accessTokenLiveTime", 0).apply()
        BaseApp.sharedPreferences.edit().putString("refreshToken", "").apply()
        BaseApp.sharedPreferences.edit().putLong("refreshTokenLiveTime", 0).apply()
    }

    private fun decodeJwtToken(token: String?): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        if (token != null && token.isNotEmpty()) {
            val splitString = token.split("\\.".toRegex()).toTypedArray()
            val base64EncodedBody = splitString[1]
            val body = String(Base64.decode(base64EncodedBody, 0))
            try {
                val element = JSONObject(body)
                map.put("exp", element.getString("exp"))
                map.put("user_id", element.getString("user_id"))
                map.put("type", element.getString("type"))
//                val element = JsonParser.parseString(body).asJsonObject
//                val element = JSONObject(body).
//                for ((key, value) in element.entrySet()) {
//                    map[key.toString().replace("\"", "")] = value.toString().split(".").first()
//                }
            } catch (e: Exception) {
            }
        }
        return map
    }

    private fun getExpiredTimeFromMap(decodedToken: Map<String, String>): Long {
        return decodedToken["exp"]?.toLong()?.times(1000) ?: 0L
    }
}
