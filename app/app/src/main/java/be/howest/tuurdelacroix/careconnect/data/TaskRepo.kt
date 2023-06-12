package be.howest.tuurdelacroix.careconnect.data

import android.os.Build
import androidx.annotation.RequiresApi
import be.howest.tuurdelacroix.careconnect.models.local.ShoppingItem
import be.howest.tuurdelacroix.careconnect.models.local.Task
import be.howest.tuurdelacroix.careconnect.models.local.TaskStatus
import be.howest.tuurdelacroix.careconnect.models.local.TaskType
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
object TaskRepo {

    private val taskList = mutableListOf<Task>()
    val today = LocalDateTime.now()

    init {
        taskList.addAll(
            listOf(
                Task("Mediamarkt",
                    TaskType.DO_SHOPPING,
                    TaskStatus.REQUESTED, LocalDateTime.of(2023, 4,17, 23,0), null),
                Task("Dreamland",
                    TaskType.DO_SHOPPING,
                    TaskStatus.REQUESTED, LocalDateTime.of(2023, 4,17, 22,0), mapOf(
                        //ShoppingItem("Pluche Beer", "Wit", "https://www.ikea.com/be/nl/images/products/djungelskog-pluchen-speelgoed-bruine-beer__0710168_pe727370_s5.jpg") to 5
                        ShoppingItem("Pluche Beer", "Wit", null) to 5
                    )),
                Task("Colruyt",
                    TaskType.DO_SHOPPING,
                    TaskStatus.REQUESTED, LocalDateTime.of(2023, 4,18, 22,0), mapOf(
                            ShoppingItem("AAA Batterijen", "Duracell", "https://cdn.webshopapp.com/shops/39288/files/418116280/duracell-oplaadbare-aaa-batterijen-750mah-4-stuks.jpg") to 1,
                            //ShoppingItem("AAA Batterijen", "Duracell", null) to 1,
                            ShoppingItem("AA Batterijen", "Duracell", "https://www.duracell.be/upload/sites/11/2020/07/1016826_alkaline_mainline-plus_AA_4_primary1.png") to 2
                            //ShoppingItem("AA Batterijen", "Duracell", null) to 2
                        )
                    ),
                Task("Koffie Drinken",
                    TaskType.ACCOMPANY,
                    TaskStatus.COMPLETED, LocalDateTime.of(2023, 4,18, 20,0), null),
            )
        )
    }

    fun getTasks(): List<Task> {
        // TODO: order first on status then on date
        return taskList.filter {task -> task.date >= today.withHour(today.hour)} .sortedBy { it.date }
    }

    fun getTasksForToday() : List<Task> {
        // This is also WITH the tasks that are earlier then the current time
        return taskList.filter {task -> task.date.toLocalDate() == today.toLocalDate()} .sortedBy { it.date.hour }
    }

    fun getFutureTasks(): List<Task> {
        return getTasks().filter {task -> task.date > today && task.date.toLocalDate() != today.toLocalDate()}
    }

}