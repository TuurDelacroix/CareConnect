package be.howest.tuurdelacroix.careconnect.models.api

import android.os.Build
import androidx.annotation.RequiresApi
import be.howest.tuurdelacroix.careconnect.models.local.TaskStatus
import be.howest.tuurdelacroix.careconnect.models.local.TaskType
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


data class PatientResponseAbstract(
    @SerializedName("id"            ) var id          : Int?    = null,
    @SerializedName("first_name"    ) var firstName   : String? = null,
    @SerializedName("last_name"     ) var lastName    : String? = null,
    @SerializedName("profile_pic"   ) var profilePic  : String? = null,
    @SerializedName("phone_number"  ) var phoneNumber : String? = null,
)
data class PatientResponseFull(
    @SerializedName("id"           ) var id          : Int?                   = null,
    @SerializedName("first_name"   ) var firstName   : String?                = null,
    @SerializedName("last_name"    ) var lastName    : String?                = null,
    @SerializedName("profile_pic"  ) var profilePic  : String?                = null,
    @SerializedName("phone_number" ) var phoneNumber : String?                = null,
    @SerializedName("headcarer"    ) var headcarer   : HeadCarer?             = HeadCarer(),
    @SerializedName("contacts"     ) var contacts    : ArrayList<Contact>    = arrayListOf(),
    @SerializedName("medication"  ) var medication : ArrayList<Medication> = arrayListOf(),
    @SerializedName("events"       ) var events      : ArrayList<EventResponse>      = arrayListOf(),
    @SerializedName("tasks"        ) var tasks       : ArrayList<Task>       = arrayListOf()
)

@RequiresApi(Build.VERSION_CODES.O)
data class EventResponse(
    @SerializedName("id"         ) var id        : Int?    = null,
    @SerializedName("title"      ) var title     : String? = null,
    @JsonAdapter(LocalDateTimeAdapter::class)
    @SerializedName("date") var date: LocalDateTime? = null
)

@RequiresApi(Build.VERSION_CODES.O)
class LocalDateTimeAdapter : JsonDeserializer<LocalDateTime> {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    // todo make sure date is correct...
    override fun deserialize(
       json: JsonElement?,
        typeOfT: Type?,
       context: JsonDeserializationContext?):
            LocalDateTime {
               if (json == null || json !is JsonPrimitive || !json.isString) {
                   return LocalDateTime.MIN
                }
                return LocalDateTime.parse(json.asString, formatter)
            }
}



data class HeadCarer (

    @SerializedName("id"           ) var id          : Int?    = null,
    @SerializedName("first_name"   ) var firstName   : String? = null,
    @SerializedName("last_name"    ) var lastName    : String? = null,
    @SerializedName("profile_pic"  ) var profilePic  : String? = null,
    @SerializedName("phone_number" ) var phoneNumber : String? = null

)

@RequiresApi(Build.VERSION_CODES.O)
data class Medication (
    @SerializedName("id") var id: Int? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("dose") var dose: String? = null,
    @SerializedName("is_taken") var isTaken: Int? = null,
    @JsonAdapter(LocalDateDeserializer::class)
    @SerializedName("schedule_date" ) var scheduleDate : LocalDate? = null,
    @JsonAdapter(LocalDateTimeDeserializer::class)
    @SerializedName("schedule_time" ) var scheduleTime : LocalTime? = null,
)

@RequiresApi(Build.VERSION_CODES.O)
class LocalDateDeserializer : JsonDeserializer<LocalDate> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDate {
        return LocalDate.parse(json.asString, formatter)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
class LocalDateTimeDeserializer : JsonDeserializer<LocalTime> {
    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalTime {
        return LocalTime.parse(json.asString, formatter)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
data class Task (

    @SerializedName("id"            ) var id           : Int?    = null,
    @SerializedName("title"         ) var title        : String? = null,
    @SerializedName("type"          ) var type         : TaskType? = null,
    @SerializedName("status"        ) var status       : TaskStatus? = null,
    @JsonAdapter(LocalDateWithTimeDeserializer::class)
    @SerializedName("date"          ) var date         : LocalDateTime? = null,
    @SerializedName("shopping_list" ) var shoppingList : ShoppingList? = null

)

@RequiresApi(Build.VERSION_CODES.O)
class LocalDateWithTimeDeserializer : JsonDeserializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime {
        return LocalDateTime.parse(json.asString, formatter)
    }
}

data class ShoppingList (

    @SerializedName("id"             ) var id            : Int?   = null,
    @SerializedName("name"           ) var name          : String? = null,
    @SerializedName("shopping_items" ) var shoppingItems : ArrayList<ShoppingItems> = arrayListOf()

)

data class ShoppingItems (

    @SerializedName("id"               ) var id             : Int?    = null,
    @SerializedName("name"             ) var name           : String? = null,
    @SerializedName("description"      ) var description    : String? = null,
    @SerializedName("image"            ) var image          : String? = null,
    @SerializedName("quantity"         ) var quantity       : Int?    = null,
    @SerializedName("shopping_list_id" ) var shoppingListId : Int?    = null

)

data class Contact (

    @SerializedName("id"           ) var id          : Int?     = null,
    @SerializedName("first_name"   ) var firstName   : String?  = null,
    @SerializedName("last_name"    ) var lastName    : String?  = null,
    @SerializedName("profile_pic"  ) var profilePic  : String?  = null,
    @SerializedName("phone_number" ) var phoneNumber : String?  = null,
    @SerializedName("reachable"    ) var reachable   : Boolean? = null

)