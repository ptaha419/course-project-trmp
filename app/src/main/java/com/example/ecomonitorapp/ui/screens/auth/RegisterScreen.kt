package com.example.ecomonitorapp.ui.screens.auth

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecomonitorapp.models.User
import com.example.ecomonitorapp.ui.forms.AlertMessage
import com.example.ecomonitorapp.ui.forms.RegisterForm
import com.example.ecomonitorapp.ui.viewmodels.auth.AuthState
import com.example.ecomonitorapp.ui.viewmodels.auth.AuthViewModel

@Composable
fun RegisterScreen(vm: AuthViewModel = viewModel(),
                   onToLogin: () -> Unit,
                   onSuccess: (User) -> Unit) {

    val state by vm.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            val user = (state as AuthState.Success).user
            vm.reset() // сначала сброс, чтобы onSuccess не сработал повторно
            onSuccess(user)
        }
    }

    when (state) {
        is AuthState.Idle -> RegisterForm(
            onRegister = { email, firstName, middleName, lastName, phoneNumber, passport, password ->
                vm.register(
                    email = email,
                    firstName = firstName,
                    middleName = middleName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    passport = passport,
                    password = password
                )
            },
            onToLogin = onToLogin
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