package com.example.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapp.data.AppDatabase
import com.example.myapp.data.AppRepository
import com.example.myapp.presentation.AppViewModel
import com.example.myapp.presentation.AppViewModelFactory
import com.example.myapp.presentation.ListScreen
import com.example.myapp.ui.theme.MyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AppRepository(database.appDao())
        val viewModelFactory = AppViewModelFactory(repository)

        enableEdgeToEdge()
        setContent {
            MyAppTheme {
                MyAppApp(viewModelFactory)
            }
        }
    }
}

@Composable
fun MyAppApp(viewModelFactory: AppViewModelFactory) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = { Icon(it.icon, contentDescription = it.label) },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val screenModifier = Modifier.padding(innerPadding)

            when (currentDestination) {
                AppDestinations.HOME -> SimpleTextScreen("Головна", screenModifier)

                AppDestinations.FAVORITES -> {
                    val viewModel: AppViewModel = viewModel(factory = viewModelFactory)
                    ListScreen(modifier = screenModifier, viewModel = viewModel)
                }

                AppDestinations.PROFILE -> SimpleTextScreen("Профіль", screenModifier)
            }
        }
    }
}

enum class AppDestinations(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.List),
    PROFILE("Profile", Icons.Default.Person),
}

@Composable
fun SimpleTextScreen(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, style = MaterialTheme.typography.headlineMedium)
    }
}