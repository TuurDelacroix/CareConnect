package be.howest.tuurdelacroix.careconnect.models.local

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class Task(
    val title: String,
    val type: TaskType,
    val status: TaskStatus,
    val date: LocalDateTime,
    var shoppingList: Map<ShoppingItem, Int>?
) : Serializable {

}