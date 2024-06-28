package com.pravin.tripwake.model.service

import android.app.Activity
import android.content.Context
import com.pravin.tripwake.model.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUserId: String
    val hasUser: Boolean

    val currentUser: Flow<User>

    suspend fun authenticate(email: String, password: String)
    suspend fun registerUser(email: String, password: String)
    suspend fun sendRecoveryEmail(email: String)
    suspend fun createAnonymousAccount()
    suspend fun linkAccount(email: String, password: String)
    suspend fun deleteAccount()
    suspend fun signOut()
    suspend fun signInWithGoogle(context: Context, idToken: String): Boolean

}