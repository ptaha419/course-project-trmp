package com.example.ecomonitorapp.ui.screens.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecomonitorapp.ui.viewmodels.account.EditAccountState
import com.example.ecomonitorapp.ui.viewmodels.account.EditAccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountScreen(
    vm: EditAccountViewModel,
    userId: Int,
    onBack: () -> Unit,
) {
    LaunchedEffect(userId) { vm.loadUser(userId) }

    val state by vm.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактировать профиль") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { vm.save(onSuccess = onBack) },
                        enabled = state.canSave
                    ) {
                        Text("Сохранить")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            state.original == null && state.isSaving.not() && state.error == null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else -> EditForm(
                state = state,
                onFirstNameChange = vm::onFirstNameChange,
                onMiddleNameChange = vm::onMiddleNameChange,
                onLastNameChange = vm::onLastNameChange,
                onEmailChange = vm::onEmailChange,
                onPhoneChange = vm::onPhoneChange,
                modifier = Modifier.fillMaxSize().padding(padding)
            )
        }
    }
}

@Composable
private fun EditForm(
    state: EditAccountState,
    onFirstNameChange: (String) -> Unit,
    onMiddleNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = state.firstName,
            onValueChange = onFirstNameChange,
            label = { Text("Имя") },
            isError = state.firstName.isNotEmpty() && !state.isFirstNameValid,
            supportingText = {
                if (state.firstName.isNotEmpty() && !state.isFirstNameValid) Text("Минимум 2 символа")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.middleName,
            onValueChange = onMiddleNameChange,
            label = { Text("Отчество") },
            isError = state.middleName.isNotEmpty() && !state.isMiddleNameValid,
            supportingText = {
                if (state.middleName.isNotEmpty() && !state.isMiddleNameValid) Text("Минимум 2 символа")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.lastName,
            onValueChange = onLastNameChange,
            label = { Text("Фамилия") },
            isError = state.lastName.isNotEmpty() && !state.isLastNameValid,
            supportingText = {
                if (state.lastName.isNotEmpty() && !state.isLastNameValid) Text("Минимум 2 символа")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            isError = state.email.isNotEmpty() && !state.isEmailValid,
            supportingText = {
                if (state.email.isNotEmpty() && !state.isEmailValid) Text("Некорректный email")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.phoneNumber,
            onValueChange = onPhoneChange,
            label = { Text("Телефон") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}