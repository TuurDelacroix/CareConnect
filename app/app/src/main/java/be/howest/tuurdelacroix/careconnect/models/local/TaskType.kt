package be.howest.tuurdelacroix.careconnect.models.local

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.CoffeeSolid
import compose.icons.lineawesomeicons.ShoppingCartSolid

enum class TaskType(val label: String, val icon: ImageVector) {
    DO_SHOPPING("Boodschappen", LineAwesomeIcons.ShoppingCartSolid),
    ACCOMPANY("Gezelschap", LineAwesomeIcons.CoffeeSolid),
}