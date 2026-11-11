package com.example.ecomonitorapp.ui.screens.support

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ecomonitorapp.data.repositories.UserRepository
import com.example.ecomonitorapp.models.UserRole
import com.example.ecomonitorapp.ui.screens.HomeScreen
import com.example.ecomonitorapp.ui.screens.account.AccountScreen
import com.example.ecomonitorapp.ui.screens.account.EditAccountScreen
import com.example.ecomonitorapp.ui.screens.reports.AllReportsScreen
import com.example.ecomonitorapp.ui.screens.reports.MyReportsScreen
import com.example.ecomonitorapp.ui.screens.reports.ReportScreen
import com.example.ecomonitorapp.ui.screens.violationCards.ViolationCardScreen
import com.example.ecomonitorapp.ui.viewmodels.account.AccountViewModel
import com.example.ecomonitorapp.ui.viewmodels.account.EditAccountViewModel
import com.example.ecomonitorapp.ui.viewmodels.reports.ReportViewModel
import com.example.ecomonitorapp.ui.viewmodels.violationCards.ViolationCardViewModel
import kotlinx.coroutines.launch

// Модель раздела меню
data class DrawerItem(val title: String, val icon: ImageVector, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(repository: UserRepository) {
    val userId = repository.getCurrentUserId()
    var userRole by remember { mutableStateOf<UserRole?>(null) }
    LaunchedEffect(userId) {
        userRole = repository.getUserRole(userId)
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    // следим за текущим маршрутом, чтобы менять заголовок
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: "home"

    val title = when {
        currentRoute?.startsWith("account") == true -> "Личный кабинет"
        currentRoute?.startsWith("editAccount") == true -> "Редактирование"
        currentRoute == "map" -> "Карта города"
        currentRoute == "report" -> "Новое обращение"
        currentRoute == "allReports" -> "Список обращений"
        currentRoute == "myReports" -> "Мои обращения"
        currentRoute?.startsWith("violation_card") == true -> "Карточка нарушения"
        else -> "EcoMonitorApp"
    }

    val drawerItems = mutableListOf(
        DrawerItem("Главная", Icons.Filled.Home, "home"),
        DrawerItem("Личный кабинет", Icons.Filled.Person, "account"),
        DrawerItem("Новое обращение", Icons.Filled.Add, "report"),
        DrawerItem("Мои обращения", Icons.AutoMirrored.Filled.List, "myReports"),
        DrawerItem("Карта города", Icons.Filled.Map, "map")
    )

    if (userRole == UserRole.MODERATOR) {
        drawerItems.add(
            DrawerItem(
                title = "Список всех обращений",
                icon = Icons.Filled.Layers,
                route = "allReports"
            )
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "EcoMonitorApp",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        icon = { Icon(item.icon, contentDescription = null) },
                        selected = currentRoute?.startsWith(item.route) == true,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route) {
                                popUpTo("home") // «назад» всегда ведёт на главную
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Открыть меню")
                        }
                    }
                )
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(padding)
            ) {
                composable("home") {
                    val userId = repository.getCurrentUserId()
                    var userRole by remember { mutableStateOf<UserRole?>(null) }
                    LaunchedEffect(userId) {
                        userRole = repository.getUserRole(userId)
                    }
                    HomeScreen(
                        userRole = userRole,
                        onOpenAccount = {
                            if (userId != -1L) navController.navigate("account?userId=$userId")
                        },
                        onOpenMap = { navController.navigate("map") },
                        onCreateReport = { navController.navigate("report") },
                        onOpenListOfReports = { navController.navigate("allReports") },
                        onOpenMyReports = { navController.navigate("myReports") }
                    )
                }
                composable(
                    "account?userId={userId}",
                    arguments = listOf(
                        navArgument("userId") {
                            type = NavType.LongType
                            defaultValue = -1L   // если id не передали — значение по умолчанию
                        }
                    )
                ) { backStackEntry ->
                    var userId = backStackEntry.arguments?.getLong("userId") ?: -1L
                    // если id не передали (переход из меню) — берём его из хранилища
                    if (userId == -1L) {
                        userId = repository.getCurrentUserId()
                    }
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
                composable("map") {
                    StubScreen("Карта города", onBack = { navController.popBackStack() })
                }
                composable("allReports") {
                    AllReportsScreen(
                        onOpenReport = { reportId ->
                            navController.navigate("report?reportId=$reportId")
                        }
                    )
                }
                composable("myReports") {
                    MyReportsScreen(
                        onOpenReport = { reportId ->
                            navController.navigate("report?reportId=$reportId")
                        },
                        onOpenDraft = { draftId ->
                            navController.navigate("report?draftId=$draftId")
                        }
                    )
                }
                composable(
                    "report?reportId={reportId}",
                    arguments = listOf(
                        navArgument("reportId") {
                            type = NavType.LongType
                            defaultValue = -1L
                        }
                    )
                ) { entry ->
                    val vm: ReportViewModel = viewModel(factory = ReportViewModel.factory(LocalContext.current))
                    LaunchedEffect(entry) {
                        val reportId = entry.arguments?.getLong("reportId") ?: -1L
                        if (reportId != -1L) vm.loadReport(reportId)
                    }
                    ReportScreen(
                        vm = vm,
                        onBack = { navController.popBackStack() },
                        onOpenCard = { cardId -> navController.navigate("violation_card/$cardId") },  // ← добавили
                    )
                }

                composable(
                    "report?draftId={draftId}",
                    arguments = listOf(
                        navArgument("draftId") {
                            type = NavType.LongType
                            defaultValue = -1L
                        }
                    )
                ) { entry ->
                    val vm: ReportViewModel = viewModel(factory = ReportViewModel.factory(LocalContext.current))
                    LaunchedEffect(entry) {
                        val draftId = entry.arguments?.getLong("draftId") ?: -1L
                        if (draftId != -1L) vm.loadDraft(draftId)
                    }
                    ReportScreen(
                        vm = vm,
                        onBack = { navController.popBackStack() },
                        onOpenCard = { cardId -> navController.navigate("violation_card/$cardId") },  // ← добавили
                    )
                }
                composable(
                    "violation_card/{cardId}",
                    arguments = listOf(navArgument("cardId") { type = NavType.LongType })
                ) { entry ->
                    val vm: ViolationCardViewModel = viewModel(factory = ViolationCardViewModel.factory(LocalContext.current))
                    ViolationCardScreen(
                        vm = vm,
                        cardId = entry.arguments?.getLong("cardId") ?: return@composable,
                        onBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}