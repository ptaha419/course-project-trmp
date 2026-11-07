package com.example.ecomonitorapp.ui.screens.auth

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecomonitorapp.ui.forms.AlertMessage
import com.example.ecomonitorapp.ui.viewmodels.auth.AuthState
import com.example.ecomonitorapp.ui.viewmodels.auth.AuthViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.runtime.LaunchedEffect
import com.example.ecomonitorapp.ui.forms.LoginForm
import com.example.ecomonitorapp.models.User

@Composable
fun LoginScreen(vm: AuthViewModel = viewModel(),
                 onToRegister: () -> Unit,
                 onSuccess: (User) -> Unit) {

    val state by vm.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            val user = (state as AuthState.Success).user
            vm.reset()
            onSuccess(user)
        }
    }

    when (state) {
        is AuthState.Idle -> LoginForm(
            onLogin = { email, password ->
                vm.login(email, password)
            },
            onToRegister = onToRegister
        )

        is AuthState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        is AuthState.Success -> { /* обрабатывается в LaunchedEffect */ }

        is AuthState.Error -> AlertMessage(message = (state as AuthState.Error).message)
    }
}