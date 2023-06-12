package be.howest.tuurdelacroix.careconnect.models

import androidx.compose.ui.graphics.vector.ImageVector
import be.howest.tuurdelacroix.careconnect.ui.screens.CareConnectScreens

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: CareConnectScreens
)
