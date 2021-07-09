package ru.bikbulatov.comeWithMe.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.bikbulatov.comeWithMe.core.domain.TokenRequest
import ru.bikbulatov.comeWithMe.core.domain.TokenResponse
import java.io.IOException

class ApiAuthenticator : Authenticator {

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        val originalRequest = response.request().newBuilder()
            .header("Authorization", "JWT " + TokenRepository.accessToken).build()
        synchronized(this) {
            return when {
                response.code() == 401 -> {
                    val newToken = getNewToken()
                    if (newToken?.code() == 200) {
                        updateTokens(newToken)
                        originalRequest.newBuilder()
                            .header("Authorization", "JWT " + newToken.body()?.access)
                            .build()
                    } else
                        null
                }
                else -> null
            }
        }
    }

    private fun updateTokens(newToken: retrofit2.Response<TokenResponse>) {
        newToken.body()?.let {
            TokenRepository.saveTokens(it.access, it.refresh)
        }
    }

    private fun getNewToken(): retrofit2.Response<TokenResponse>? {
        return runBlocking {
            try {
                NetworkHolder.authenticatorRetrofitClient.create(AuthApi::class.java)
                    .refreshTokens(refreshToken = TokenRequest(TokenRepository.refreshToken))
                    .execute()
            } catch (e: IOException) {
                null
            }
        }
    }
}
