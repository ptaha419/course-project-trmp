package com.example.ecomonitorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import com.example.ecomonitorapp.ui.screens.account.AccountScreen
import com.example.ecomonitorapp.ui.screens.HomeScreen
import com.example.ecomonitorapp.ui.screens.account.EditAccountScreen
import com.example.ecomonitorapp.ui.screens.auth.LoginScreen
import com.example.ecomonitorapp.ui.screens.auth.RegisterScreen
import com.example.ecomonitorapp.ui.viewmodels.account.AccountViewModel
import com.example.ecomonitorapp.ui.viewmodels.account.EditAccountViewModel

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
                        startDestination = "register", // первый экран при запуске
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("register") {
                            RegisterScreen(
                                onSuccess = { user ->
                                    repository.saveCurrentUserId(user.id)
                                    // переход на главный экран после успешной регистрации
                                    navController.navigate("home")
                                },
                                onToLogin = { navController.navigate("login") }
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                onSuccess = { user ->
                                    repository.saveCurrentUserId(user.id)
                                    // переход на главный экран после авторизации
                                    navController.navigate("home")
                                },
                                onToRegister = { navController.navigate("register") }
                            )
                        }
                        composable("home") {
                            val userId = repository.getCurrentUserId()
                            HomeScreen(
                                onOpenAccount = {
                                    if (userId != -1L) navController.navigate("account/$userId")
                                },
                                onOpenMap = { navController.navigate("map") },
                                onOpenAppeal = { navController.navigate("appeal") },
                                onOpenListOfAppeals = { navController.navigate("listOfAppeals")},
                                onOpenMyAppeals = { navController.navigate("myAppeals")}
                            )
                        }
                        composable(
                            "account/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
                            val viewModel: AccountViewModel = viewModel(
                                factory = AccountViewModel.factory(LocalContext.current.applicationContext)
                            )
                            AccountScreen(
                                vm = viewModel,
                                userId = userId,
                                onEditClick = { navController.navigate("editAccount/$userId") }
                            )
                        }
                        composable(
                            "editAccount/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
                            EditAccountScreen(
                                userId = userId,
                                onBack = { navController.popBackStack() },
                                vm = viewModel(
                                    factory = EditAccountViewModel.factory(LocalContext.current)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}