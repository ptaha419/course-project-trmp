package com.example.ecomonitorapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecomonitorapp.ui.forms.RegisterForm
import com.example.ecomonitorapp.ui.viewmodels.AuthState
import com.example.ecomonitorapp.ui.viewmodels.AuthViewModel

@Composable
fun RegisterScreen(viewModel: AuthViewModel,
                   onSuccess: () -> Unit) {
}