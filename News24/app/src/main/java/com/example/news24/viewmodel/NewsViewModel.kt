package com.example.news24.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NewsViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    fun onGoogleSignIn(idToken: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
                Log.d("NewsViewModel", "signInWithGoogle: Success")
                onSuccess()
            } catch (e: Exception) {
                Log.e("NewsViewModel", "signInWithGoogle: Failure", e)
                onFailure(e)
            }
        }
    }
}
