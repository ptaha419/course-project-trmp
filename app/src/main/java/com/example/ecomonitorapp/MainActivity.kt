package com.example.ecomonitorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import com.example.ecomonitorapp.ui.theme.EcoMonitorAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ecomonitorapp.data.repositories.UserRepository
import com.example.ecomonitorapp.models.UserRole
import com.example.ecomonitorapp.ui.screens.account.AccountScreen
import com.example.ecomonitorapp.ui.screens.HomeScreen
import com.example.ecomonitorapp.ui.screens.account.EditAccountScreen
import com.example.ecomonitorapp.ui.screens.auth.LoginScreen
import com.example.ecomonitorapp.ui.screens.auth.RegisterScreen
import com.example.ecomonitorapp.ui.screens.reports.ReportScreen
import com.example.ecomonitorapp.ui.screens.support.MainScreen
import com.example.ecomonitorapp.ui.viewmodels.account.AccountViewModel
import com.example.ecomonitorapp.ui.viewmodels.account.EditAccountViewModel
import com.example.ecomonitorapp.ui.viewmodels.reports.ReportViewModel
import com.example.ecomonitorapp.ui.screens.support.StubScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoMonitorAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val repository = UserRepository(applicationContext)

                    NavHost(
                        navController = navController,
                        startDestination = "login", // первый экран при запуске
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("register") {
                            RegisterScreen(
                                onSuccess = { user ->
                                    repository.saveCurrentUserId(user.id)
                                    navController.navigate("main") {
                                        popUpTo("login") { inclusive = true } // чтобы «назад» не возвращал на логин
                                    }
                                },
                                onToLogin = { navController.navigate("login") }
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                onSuccess = { user ->
                                    repository.saveCurrentUserId(user.id)
                                    navController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onToRegister = { navController.navigate("register") }
                            )
                        }
                        composable("main") {
                            MainScreen(repository = repository)
                        }
                    }
                }
            }
        }
    }
}