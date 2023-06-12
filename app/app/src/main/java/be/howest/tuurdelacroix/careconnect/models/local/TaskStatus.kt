package be.howest.tuurdelacroix.careconnect.models.local

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.CheckCircleSolid
import compose.icons.lineawesomeicons.PencilAltSolid
import compose.icons.lineawesomeicons.StopwatchSolid

enum class TaskStatus(val label: String, val icon: ImageVector) {
    REQUESTED("Aangevraagd", LineAwesomeIcons.PencilAltSolid),
    IN_PROGRESS("In Uitvoering", LineAwesomeIcons.StopwatchSolid),
    COMPLETED("Uitgevoerd", LineAwesomeIcons.CheckCircleSolid);

    override fun toString(): String {
        return label
    }
}