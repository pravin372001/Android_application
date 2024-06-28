package com.pravin.tripwake.screens.register

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import com.pravin.tripwake.R
import com.pravin.tripwake.Screen
import com.pravin.tripwake.model.service.AccountService
import com.pravin.tripwake.util.ext.isValidEmail
import com.pravin.tripwake.util.ext.isValidPassword
import com.pravin.tripwake.util.ext.passwordMatches
import com.pravin.tripwake.util.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.pravin.tripwake.R.string as AppText

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {
    var uiState = mutableStateOf(RegisterUiState())
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

    fun onRepeatPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(repeatPassword = newValue)
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(AppText.email_error)
            return
        }

        if (!password.isValidPassword()) {
            SnackbarManager.showMessage(AppText.password_error)
            return
        }

        if (!password.passwordMatches(uiState.value.repeatPassword)) {
            SnackbarManager.showMessage(AppText.password_match_error)
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            accountService.registerUser(email, password)
            openAndPopUp(Screen.Main.route, Screen.Login.route)
        }
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp(Screen.Login.route, Screen.Register.route)
    }

    fun onGoogleClick(context: Context, openAndPopUp: (String, String) -> Unit, idToken: String) {
        CoroutineScope(Dispatchers.Main).launch {
            accountService.signInWithGoogle(context,idToken)
            openAndPopUp(Screen.Main.route, Screen.Register.route)
        }
    }
}