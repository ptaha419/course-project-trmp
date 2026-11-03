package com.example.ecomonitorapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.ecomonitorapp.ui.forms.AlertMessage
import com.example.ecomonitorapp.ui.forms.RegisterForm
import com.example.ecomonitorapp.ui.viewmodels.AuthState
import com.example.ecomonitorapp.ui.viewmodels.AuthViewModel

@Composable
fun RegisterScreen(vm: AuthViewModel = viewModel(),
                   onToLogin: () -> Unit,
                   onSuccess: () -> Unit) {

    val state by vm.stateFlow.collectAsStateWithLifecycle()

    when(state) {
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

        is AuthState.Loading -> Box (
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        is AuthState.Success -> onSuccess()

        is AuthState.Error -> AlertMessage(message = (state as AuthState.Error).message)
    }
}