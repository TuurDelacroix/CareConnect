package be.howest.tuurdelacroix.careconnect.data

import be.howest.tuurdelacroix.careconnect.models.BottomNavItem
import be.howest.tuurdelacroix.careconnect.ui.screens.CareConnectScreens
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.*

object NavigationRepo {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Start",
            icon = LineAwesomeIcons.HomeSolid,
            route = CareConnectScreens.Start
        ),
        BottomNavItem(
            label = "Medicatie",
            icon = LineAwesomeIcons.PillsSolid,
            route = CareConnectScreens.Medication
        ),
        BottomNavItem(
            label = "Te Doen",
            icon = LineAwesomeIcons.ListAltSolid,
            route = CareConnectScreens.ToDo
        ),
        BottomNavItem(
            label = "Verzorger(s)",
            icon = LineAwesomeIcons.UsersSolid,
            route = CareConnectScreens.Carer
        )
    )

    val MenuNavItems = listOf(
        BottomNavItem(
            label = "Mijn Medicatie",
            icon = LineAwesomeIcons.PillsSolid,
            route = CareConnectScreens.Medication
        ),
        BottomNavItem(
            label = "Te Doen",
            icon = LineAwesomeIcons.TasksSolid,
            route = CareConnectScreens.ToDo
        ),
        BottomNavItem(
            label = "Mijn Verzorger(s)",
            icon = LineAwesomeIcons.StethoscopeSolid,
            route = CareConnectScreens.Carer
        )
    )
}