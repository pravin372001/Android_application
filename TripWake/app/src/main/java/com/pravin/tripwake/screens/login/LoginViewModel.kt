package com.pravin.tripwake.screens.login

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pravin.tripwake.model.service.AccountService
import com.pravin.tripwake.Screen
import com.pravin.tripwake.util.ext.isValidEmail
import com.pravin.tripwake.util.snackbar.SnackbarManager
import com.pravin.tripwake.util.snackbar.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.pravin.tripwake.R.string as AppText

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {

    var uiState = mutableStateOf(LoginUiState())
        private set

    var isLoginUiState = mutableStateOf(false)
        private set

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(AppText.email_error)
            return
        }

        if (password.isBlank()) {
            SnackbarManager.showMessage(AppText.empty_password_error)
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try{
                accountService.authenticate(email, password)
            } catch (e: Exception){
                SnackbarManager.showMessage(SnackbarMessage.StringSnackbar("Invalid Credentials"))
                return@launch
            }
            openAndPopUp(Screen.Main.route, Screen.Login.route)
        }
    }

    fun onForgotPasswordClick() {
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(AppText.email_error)
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            accountService.sendRecoveryEmail(email)
            SnackbarManager.showMessage(AppText.recovery_email_sent)
        }
    }

    fun onRegisterClick(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp(Screen.Register.route, Screen.Login.route)
    }

    fun onGoogleClick(context: Context, idToken:String) {
        viewModelScope.launch {
            isLoginUiState.value = accountService.signInWithGoogle(context, idToken)
        }
    }
}
