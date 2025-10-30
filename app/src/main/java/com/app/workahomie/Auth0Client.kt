package com.app.workahomie

import android.app.Activity
import android.content.Context
import android.util.Log
import com.app.workahomie.data.FcmTokenBody
import com.app.workahomie.network.HostApi
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Auth0Client(
    private val activity: Activity,
    domain: String,
    clientId: String,
    private val scheme: String
) {
    private val auth0 = Auth0.getInstance(clientId, domain)
    private val authApiClient = AuthenticationAPIClient(auth0)
    private val credentialsManager = CredentialsManager(authApiClient, SharedPreferencesStorage(activity))

    fun login(
        onSuccess: (accessToken: String, user: UserProfile) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        WebAuthProvider.login(auth0)
            .withScheme(scheme)
            .withScope("openid profile email")
            .withAudience("https://workahomie.api.com")
            .start(activity, object : Callback<Credentials, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    onFailure(error.getDescription())
                }

                override fun onSuccess(result: Credentials) {
                    credentialsManager.saveCredentials(result)

                    val token = result.accessToken
                    HostApi.setToken(token)

                    authApiClient.userInfo(token)
                        .start(object : Callback<UserProfile, AuthenticationException> {
                            override fun onFailure(error: AuthenticationException) {
                                onFailure(error.getDescription())
                            }

                            override fun onSuccess(result: UserProfile) {
                                onSuccess(token, result)

                                // Send saved FCM token to backend
                                val fcmToken = activity.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                    .getString("fcm_token", null)

                                if (!fcmToken.isNullOrEmpty()) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            HostApi.retrofitService.updateFcmToken(FcmTokenBody(fcmToken))
                                        } catch (e: Exception) {
                                            Log.e("FCM", "Failed to send FCM token: ${e.message}")
                                        }
                                    }
                                }
                            }
                        })
                }
            })
    }

    fun logout(
        onSuccess: () -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        WebAuthProvider.logout(auth0)
            .withScheme(scheme)
            .start(activity, object : Callback<Void?, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    onFailure(error.getDescription())
                }

                override fun onSuccess(result: Void?) {
                    credentialsManager.clearCredentials()
                    onSuccess()
                }
            })
    }

    fun hasValidSession(): Boolean {
        return credentialsManager.hasValidCredentials()
    }

    fun getSavedCredentials(
        onSuccess: (accessToken: String, user: UserProfile) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        if (!credentialsManager.hasValidCredentials()) {
            onFailure("No saved credentials")
            return
        }

        credentialsManager.getCredentials(object : Callback<Credentials, CredentialsManagerException> {
            override fun onFailure(error: CredentialsManagerException) {
                onFailure(error.message ?: "Unknown credentials error")
            }

            override fun onSuccess(result: Credentials) {
                val token = result.accessToken
                HostApi.setToken(token)
                authApiClient.userInfo(token)
                    .start(object : Callback<UserProfile, AuthenticationException> {
                        override fun onFailure(error: AuthenticationException) {
                            onFailure(error.getDescription())
                        }

                        override fun onSuccess(result: UserProfile) {
                            onSuccess(token, result)
                        }
                    })
            }
        })
    }

}
