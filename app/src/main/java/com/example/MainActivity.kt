package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppViewModel
import com.example.ui.screens.*
import com.example.ui.theme.BorderColor
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.SurfaceDarkBar
import com.example.ui.theme.SurfaceDarkCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrendGreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppLayout()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppLayout() {
    val viewModel: AppViewModel = viewModel()
    val userProfile by viewModel.userProfileFlow.collectAsState(initial = null)

    // Current navigation state destination
    // Possible values: "home", "allocation", "portfolio", "history", "settings", "disclaimer", "tips"
    var currentScreen by remember { mutableStateOf("home") }

    // Navigation back stack representation for special deep views
    val navigateTo: (String) -> Unit = { screen ->
        currentScreen = screen
    }

    // Intercept hardware Android back keys
    BackHandler(enabled = currentScreen != "home") {
        currentScreen = when (currentScreen) {
            "allocation" -> "home"
            "disclaimer" -> "settings"
            "tips" -> "settings"
            else -> "home"
        }
    }

    val showBars = currentScreen in listOf("home", "portfolio", "history", "settings")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (showBars) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Logo Box
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PrimaryAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "G",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp
                                )
                            }

                            // App Details
                            Column {
                                Text(
                                    text = "GrowKaro AI",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(TrendGreen)
                                    )
                                    Text(
                                        text = "Online Calibrator Ready",
                                        color = TrendGreen,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { currentScreen = "settings" },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "User Settings Profiler",
                                tint = TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SurfaceDarkBar,
                        titleContentColor = TextPrimary
                    )
                )
            }
        },
        bottomBar = {
            if (showBars) {
                NavigationBar(
                    containerColor = SurfaceDarkBar,
                    tonalElevation = 8.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    val items = listOf(
                        NavigationItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
                        NavigationItem("portfolio", "Portfolio", Icons.Filled.PieChart, Icons.Outlined.PieChart),
                        NavigationItem("history", "History", Icons.Filled.History, Icons.Outlined.History),
                        NavigationItem("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
                    )

                    items.forEach { item ->
                        val isSelected = currentScreen == item.id
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentScreen = item.id },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.activeIcon else item.inactiveIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SurfaceDarkBar,
                                unselectedIconColor = TextMuted,
                                selectedTextColor = PrimaryAccent,
                                unselectedTextColor = TextMuted,
                                indicatorColor = PrimaryAccent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "screen_transitions") { screen ->
                when (screen) {
                    "home" -> HomeScreen(
                        viewModel = viewModel,
                        userProfile = userProfile,
                        onNavigateToAllocation = { currentScreen = "allocation" },
                        onNavigateToDisclaimer = { currentScreen = "disclaimer" },
                        onNavigateToTips = { currentScreen = "tips" }
                    )
                    "allocation" -> AllocationScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = "home" }
                    )
                    "portfolio" -> PortfolioScreen(
                        viewModel = viewModel
                    )
                    "history" -> HistoryScreen(
                        viewModel = viewModel
                    )
                    "settings" -> SettingsScreen(
                        viewModel = viewModel,
                        userProfile = userProfile,
                        onNavigateToDisclaimer = { currentScreen = "disclaimer" },
                        onNavigateToTips = { currentScreen = "tips" }
                    )
                    "disclaimer" -> DisclaimerScreen(
                        onNavigateBack = { currentScreen = "settings" }
                    )
                    "tips" -> EducationalTips(
                        onNavigateBack = { currentScreen = "settings" }
                    )
                }
            }
        }
    }
}

data class NavigationItem(
    val id: String,
    val label: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
)
