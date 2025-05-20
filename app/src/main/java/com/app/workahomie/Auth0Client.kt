package com.app.workahomie

import android.app.Activity
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials

class Auth0Client(
    private val activity: Activity,
    private val domain: String,
    private val clientId: String,
    private val scheme: String
) {
    private val auth0 = Auth0.getInstance(clientId, domain)

    fun login(
        onSuccess: (Credentials) -> Unit,
        onFailure: (AuthenticationException) -> Unit
    ) {
        WebAuthProvider.login(auth0)
            .withScheme(scheme)
            .withScope("openid profile email")
            .start(activity, object : Callback<Credentials, AuthenticationException> {
                override fun onSuccess(result: Credentials) = onSuccess(result)
                override fun onFailure(error: AuthenticationException) = onFailure(error)
            })
    }

    fun logout(
        onSuccess: () -> Unit,
        onFailure: (AuthenticationException) -> Unit
    ) {
        WebAuthProvider.logout(auth0)
            .withScheme(scheme)
            .start(activity, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(result: Void?) = onSuccess()
                override fun onFailure(error: AuthenticationException) = onFailure(error)
            })
    }
}
