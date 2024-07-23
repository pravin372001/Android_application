package com.example.znews.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.znews.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(private val googleSignInClient: GoogleSignInClient, private val context: Context) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _navigateToListScreen = MutableLiveData<Boolean>()
    val navigateToListScreen: LiveData<Boolean> = _navigateToListScreen

    init {
        auth.addAuthStateListener { auth ->
            _user.value = auth.currentUser
            Log.d(TAG, "Auth state changed: ${auth.currentUser?.uid}")
        }
    }

    fun setNavigateToListScreen(value: Boolean) {
        _navigateToListScreen.value = value
    }

    fun login(idToken: String){
        viewModelScope.launch {
            val success = signInWithGoogle(idToken)
            if (success) {
                _navigateToListScreen.value = true
            }
        }
    }

    private suspend fun signInWithGoogle(idToken: String): Boolean {
        if (context !is Activity) {
            throw IllegalArgumentException("Context must be an Activity")
        }
        var success: Boolean = false
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(idToken)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        withContext(Dispatchers.Main) {
            try {
                val result = credentialManager.getCredential(request = request, context = context)
                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnSuccessListener {
                        Log.d(TAG, "signInWithGoogle: Success")
                        _user.value = auth.currentUser
                        success = true
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, "signInWithGoogle: Failure", e)
                    }

            } catch (e: Exception) {
                credentialManager.clearCredentialState(
                    ClearCredentialStateRequest()
                )
                Log.e(TAG, "signInWithGoogle: Exception", e)
            }
        }
        delay(1000)
        return success
    }

    fun logout() {
        auth.signOut()
        googleSignInClient.signOut()
        _user.value = null
    }

    companion object {
        private const val TAG = "NewsViewModel"
        const val RC_SIGN_IN = 9001
    }
}

class NewsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(googleSignInClient, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}