package com.example.ecomonitorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.ecomonitorapp.ui.theme.EcoMonitorAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecomonitorapp.ui.screens.HomeScreen
import com.example.ecomonitorapp.ui.screens.LoginScreen
import com.example.ecomonitorapp.ui.screens.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoMonitorAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "register", // первый экран при запуске
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("register") {
                            RegisterScreen(
                                onSuccess = {
                                    // переход на главный экран после успешной регистрации
                                    navController.navigate("home")
                                },
                                onToLogin = { navController.navigate("login") }
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                onSuccess = {
                                    // переход на главный экран после авторизации
                                    navController.navigate("home")
                                },
                                onToRegister = { navController.navigate("register") }
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                onOpenAccount = { navController.navigate("account") },
                                onOpenMap = { navController.navigate("map") },
                                onOpenAppeal = { navController.navigate("appeal") }
                            )
                        }
                    }
                }
            }
        }
    }
}