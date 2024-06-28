package com.pravin.tripwake.screens.register

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pravin.tripwake.R
import com.pravin.tripwake.R.string as AppText
import com.pravin.tripwake.screens.composable.*
import com.pravin.tripwake.util.ext.basicButton
import com.pravin.tripwake.util.ext.fieldModifier
import com.pravin.tripwake.ui.theme.TripWakeTheme

@Composable
fun RegisterScreen(
    openAndPopUp: (String, String) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val context = LocalContext.current as Activity
    RegisterScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRepeatPasswordChange = viewModel::onRepeatPasswordChange,
        onSignUpClick = { viewModel.onSignUpClick(openAndPopUp) },
        onSignInClick = { viewModel.onSignInClick(openAndPopUp)  },
        onGoogleClick = { viewModel.onGoogleClick(context, openAndPopUp, idToken = it) }
    )
}

@Composable
fun RegisterScreenContent(
    modifier: Modifier = Modifier,
    uiState: RegisterUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit,
    onGoogleClick: (idToken: String) -> Unit
) {
    val fieldModifier = Modifier.fieldModifier()
    val clientId = stringResource(R.string.google_web_client_id)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.fieldModifier())
        EmailField(uiState.email, onEmailChange, fieldModifier)
        PasswordField(uiState.password, onPasswordChange, fieldModifier)
        RepeatPasswordField(uiState.repeatPassword, onRepeatPasswordChange, fieldModifier)

        BasicButton(AppText.create_account, Modifier.basicButton()) {
            onSignUpClick()
        }
        BasicTextButton(AppText.sign_in, Modifier.basicButton()) {
            onSignInClick()
        }

        Text(
            text = "or",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Button(
            onClick = { onGoogleClick(clientId) },
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors()
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp),
                painter = painterResource(id = R.drawable.google),
                contentDescription = null
            )
            Text(text = "Google")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val uiState = RegisterUiState(
        email = "email@test.com"
    )

    TripWakeTheme {
        RegisterScreenContent(
            uiState = uiState,
            onEmailChange = { },
            onPasswordChange = { },
            onRepeatPasswordChange = { },
            onSignUpClick = { },
            onSignInClick = { },
            onGoogleClick = { }
        )
    }
}