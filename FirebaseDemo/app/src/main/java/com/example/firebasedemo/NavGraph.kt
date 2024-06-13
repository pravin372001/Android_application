package com.example.firebasedemo

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import com.example.firebasedemo.R.string as AppText

enum class Screens{
    Login,
    Home
}

@Composable
fun NavGraph(startDestination: String, auth: FirebaseAuth) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screens.Login.name) {
            LoginScreen(
                onSignInClick = {
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(context.resources.getString(AppText.google_web_client_id))
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    scope.launch {
                        try {
                            val result = credentialManager.getCredential(request = request, context = context)
                            val credential = result.credential
                            val googleIdTokenCredential = GoogleIdTokenCredential
                                .createFrom(credential.data)
                            val googleIdToken = googleIdTokenCredential.idToken
                            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener { task ->
                                    if(task.isSuccessful) {
                                        navController.popBackStack()
                                        navController.navigate(Screens.Home.name)

                                    }
                                }
                        } catch(e: Exception) {
                            Toast.makeText(context, "Failed to Sign In", Toast.LENGTH_SHORT).show()
                            Log.e("NavGraph", "Failed to Sign In: $e")
                            e.printStackTrace()
                        }
                    }
                }
            )
        }
        composable(Screens.Home.name) {
            HomeScreen(
                currentUser = auth.currentUser,
                onSignOutClick = {
                    auth.signOut()
                    scope.launch {
                        credentialManager.clearCredentialState(
                            ClearCredentialStateRequest()
                        )
                    }
                    navController.popBackStack()
                    navController.navigate(Screens.Login.name)
                }
            )
        }
    }
}