package com.example.ecomonitorapp

import android.os.Build
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
import androidx.preference.PreferenceManager
import com.example.ecomonitorapp.data.managers.SessionManager
import com.example.ecomonitorapp.data.repositories.ReportRepository
import com.example.ecomonitorapp.data.repositories.UserRepository
import com.example.ecomonitorapp.ui.screens.auth.LoginScreen
import com.example.ecomonitorapp.ui.screens.auth.RegisterScreen
import com.example.ecomonitorapp.ui.screens.support.MainScreen
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // инициализация конфигурации osmdroid
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        Configuration.getInstance().load(applicationContext, prefs)
        Configuration.getInstance().userAgentValue =
            "ViolationReport/1.0 (Android ${Build.VERSION.RELEASE}; contact@example.com)"
        setContent {
            EcoMonitorAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    val userRepository = UserRepository(applicationContext)
                    val reportRepository =
                        ReportRepository(applicationContext, SessionManager(applicationContext))

                    NavHost(
                        navController = navController,
                        startDestination = "login", // первый экран при запуске
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("register") {
                            RegisterScreen(
                                onSuccess = {
                                    navController.navigate("main") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onToLogin = { navController.navigate("login") }
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                onSuccess = {
                                    navController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onToRegister = { navController.navigate("register") }
                            )
                        }
                        composable("main") {
                            MainScreen(
                                userRepository = userRepository,
                                reportRepository = reportRepository
                            )
                        }
                    }
                }
            }
        }
    }
}