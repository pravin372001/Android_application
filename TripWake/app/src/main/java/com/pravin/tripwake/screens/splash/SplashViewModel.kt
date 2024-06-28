package com.pravin.tripwake.screens.splash

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pravin.tripwake.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {

    val isLoggedIn = mutableStateOf(
        accountService.hasUser
    )
}