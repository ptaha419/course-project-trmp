package com.example.ecomonitorapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecomonitorapp.ui.forms.AlertMessage
import com.example.ecomonitorapp.ui.viewmodels.AuthState
import com.example.ecomonitorapp.ui.viewmodels.AuthViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.ecomonitorapp.ui.forms.LoginForm

@Composable
fun LoginScreen(
    vm: AuthViewModel = viewModel(),
    onSuccess: () -> Unit,
    onToRegister: () -> Unit
) {
    val state by vm.stateFlow.collectAsStateWithLifecycle()

    when (state) {
        is AuthState.Idle -> Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginForm(
                onLogin = { email, password ->
                    vm.login(email, password)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onToRegister,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Нет аккаунта? Зарегистрироваться")
            }
        }

        is AuthState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        is AuthState.Success -> {
            // переход на главный экран, state.user содержит данные пользователя
            onSuccess()
        }

        is AuthState.Error -> AlertMessage(message = (state as AuthState.Error).message)
    }


    Text(
        text = "Нет аккаунта? Зарегистрироваться",
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.clickable { onToRegister() } // вызов перехода
    )
}