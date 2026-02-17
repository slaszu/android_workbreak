package pl.slaszu.workbreak.ui.element

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row // Added import
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.slaszu.workbreak.domain.Days
import pl.slaszu.workbreak.domain.model.WorkDay
import pl.slaszu.workbreak.ui.ListRouting
import pl.slaszu.workbreak.ui.SettingRoute
import pl.slaszu.workbreak.ui.screen.DayEditComposable
import pl.slaszu.workbreak.ui.theme.WorkBreakTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarElement(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    showBadge: Boolean = false,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isMainScreen = currentDestination?.hasRoute(ListRouting::class) == true

    val scope = rememberCoroutineScope()
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            while (true) {
                delay(500)
                visible = !visible
            }
        }
    }

    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior, // Łączymy zachowanie z komponentem
        colors = TopAppBarDefaults.topAppBarColors(// Kolor, gdy lista jest na samej górze
            containerColor = MaterialTheme.colorScheme.background,
            // Kolor, gdy lista "wchodzi" pod TopBar (dodajemy lekką przezroczystość)
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                .copy(alpha = 0.85f),
            navigationIconContentColor = Color.Unspecified,
            titleContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = Color.Unspecified
        ),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Work Break",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                if (isMainScreen) {
                    Text(
                        text = "Your Schedule",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            if (!isMainScreen) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            }
        },
        actions = {
            BadgedBox(
                badge = {
                    if (showBadge) {
                        // Wrapped AnimatedVisibility in a Row to provide RowScope
                        Row {
                            AnimatedVisibility(
                                visible = visible,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Badge(modifier = Modifier.size(10.dp))
                            }
                        }
                    }
                }
            ) {
                IconButton(onClick = {
                    navController.navigate(SettingRoute)
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Menu"
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TopBarElementPreview() {
    WorkBreakTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopBarElement(
                    navController = NavHostController(LocalContext.current),
                    scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                )
            }
        ) {
            Column(
                modifier = Modifier.padding(it)
            ) {
                DayEditComposable(
                    workDay = WorkDay.create(Days.MONDAY.dayOfWeek),
                    onSave = {

                    }
                )
            }
        }
    }
}
