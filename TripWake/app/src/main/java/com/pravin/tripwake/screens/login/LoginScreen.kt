package com.pravin.tripwake.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pravin.tripwake.R
import com.pravin.tripwake.Screen
import com.pravin.tripwake.screens.composable.BasicButton
import com.pravin.tripwake.screens.composable.BasicTextButton
import com.pravin.tripwake.screens.composable.EmailField
import com.pravin.tripwake.screens.composable.PasswordField
import com.pravin.tripwake.ui.theme.TripWakeTheme
import com.pravin.tripwake.util.ext.basicButton
import com.pravin.tripwake.util.ext.fieldModifier
import com.pravin.tripwake.util.ext.textButton
import kotlin.reflect.KFunction0
import com.pravin.tripwake.R.string as AppText

@Composable
fun LoginScreen(
    openAndPopUp: (String, String) -> Unit,
    clearAndNavigate: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val context = LocalContext.current
    val isLoginUiState by remember {
        viewModel.isLoginUiState
    }

    LoginScreenContent(
        uiState = uiState,
        onEmailChange = { viewModel.onEmailChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onSignInClick = { viewModel.onSignInClick(openAndPopUp) },
        onForgotPasswordClick = viewModel::onForgotPasswordClick,
        onRegisterClick = { viewModel.onRegisterClick(openAndPopUp) },
        onGoogleClick = {
            viewModel.onGoogleClick(context = context, idToken = it)
        }
    )
    LaunchedEffect(key1 = true) {
        if (isLoginUiState){
            clearAndNavigate(Screen.Main.route)
        }
    }
}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onGoogleClick: (idToken: String) -> Unit
) {
    val idToken = stringResource(id = R.string.google_web_client_id)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
            )
        Spacer(modifier = Modifier.fieldModifier())
        EmailField(uiState.email, onEmailChange, Modifier.fieldModifier())
        PasswordField(uiState.password, onPasswordChange, Modifier.fieldModifier())

        BasicButton(AppText.sign_in, Modifier.basicButton()) { onSignInClick() }
        BasicTextButton(AppText.register, Modifier.textButton()) { onRegisterClick() }

        BasicTextButton(AppText.forgot_password, Modifier.textButton()) {
            onForgotPasswordClick()
        }
        Text(
            text = "or",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Button(
            onClick = { onGoogleClick(idToken) },
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
private fun LoginScreenPreview() {
    TripWakeTheme {
        LoginScreenContent(
            uiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onSignInClick = {},
            onForgotPasswordClick = {},
            onRegisterClick = {},
            onGoogleClick = {}
        )
    }
}

