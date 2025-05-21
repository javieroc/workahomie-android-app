package com.app.workahomie

import android.app.Activity
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile

class Auth0Client(
    private val activity: Activity,
    private val domain: String,
    private val clientId: String,
    private val scheme: String
) {
    private val auth0 = Auth0.getInstance(clientId, domain)
    private val authApiClient = AuthenticationAPIClient(auth0)

    fun login(
        onSuccess: (accessToken: String, user: UserProfile) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        WebAuthProvider.login(auth0)
            .withScheme(scheme)
            .withScope("openid profile email")
            .start(activity, object : Callback<Credentials, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    onFailure(error.getDescription())
                }

                override fun onSuccess(result: Credentials) {
                    val token = result.accessToken

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
                    onSuccess()
                }
            })
    }
}
