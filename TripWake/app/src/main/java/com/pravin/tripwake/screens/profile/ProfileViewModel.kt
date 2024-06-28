package com.pravin.tripwake.screens.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.pravin.tripwake.Screen
import com.pravin.tripwake.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val accountService: AccountService,
    private val auth: FirebaseAuth,
) : ViewModel() {

    val currentUser = auth.currentUser

    fun signOut(clearAndNavigate: (String) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            accountService.signOut()
            clearAndNavigate(Screen.Login.route)
        }
    }
}
