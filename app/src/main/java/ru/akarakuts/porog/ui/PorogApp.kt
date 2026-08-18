/** PorogApp — навигация: главный взгляд без app bar, остальные экраны с шапкой. */
package ru.akarakuts.porog.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.akarakuts.porog.R
import ru.akarakuts.porog.ui.about.AboutScreen
import ru.akarakuts.porog.ui.help.HelpScreen
import ru.akarakuts.porog.ui.leave.LeaveScreen
import ru.akarakuts.porog.ui.settings.SettingsScreen

private const val ROUTE_LEAVE = "leave"
private const val ROUTE_SETTINGS = "settings"
private const val ROUTE_HELP = "help"
private const val ROUTE_ABOUT = "about"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PorogApp(vm: PorogViewModel) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: ROUTE_LEAVE
    val title = when (route) {
        ROUTE_SETTINGS -> stringResource(R.string.nav_settings)
        ROUTE_HELP -> stringResource(R.string.nav_help)
        ROUTE_ABOUT -> stringResource(R.string.nav_about)
        else -> stringResource(R.string.app_name)
    }
    val onHome = route == ROUTE_LEAVE
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (!onHome) {
                CenterAlignedTopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.nav_back),
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = ROUTE_LEAVE,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            composable(ROUTE_LEAVE) {
                LeaveScreen(
                    vm = vm,
                    onHelp = { nav.navigate(ROUTE_HELP) },
                    onSettings = { nav.navigate(ROUTE_SETTINGS) },
                )
            }
            composable(ROUTE_SETTINGS) {
                SettingsScreen(
                    vm = vm,
                    onHelp = { nav.navigate(ROUTE_HELP) },
                    onAbout = { nav.navigate(ROUTE_ABOUT) },
                )
            }
            composable(ROUTE_HELP) { HelpScreen(onAbout = { nav.navigate(ROUTE_ABOUT) }) }
            composable(ROUTE_ABOUT) { AboutScreen() }
        }
    }
}
