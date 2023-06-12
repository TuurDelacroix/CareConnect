package be.howest.tuurdelacroix.careconnect.network
import be.howest.tuurdelacroix.careconnect.models.api.Contact
import be.howest.tuurdelacroix.careconnect.models.api.EventResponse
import be.howest.tuurdelacroix.careconnect.models.api.HeadCarer
import be.howest.tuurdelacroix.careconnect.models.api.Medication
import be.howest.tuurdelacroix.careconnect.models.api.PatientResponseAbstract
import be.howest.tuurdelacroix.careconnect.models.api.ShoppingItems
import be.howest.tuurdelacroix.careconnect.models.api.Task
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


private const val BASE_URL = "http://192.168.56.56/api/"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()


interface CareConnectAPIService {
    @GET("current-user")
    suspend fun getCurrentUser(): PatientResponseAbstract

    @GET("headcarer")
    suspend fun getHeadCarer(): HeadCarer

    @GET("events/today")
    suspend fun getEvents(): List<EventResponse>

    // Contacts
    @GET("contacts")
    suspend fun getContacts(): List<Contact>

    @PUT("contacts/{id}/toggle-reachable")
    suspend fun toggleContactReachable(@Path("id") id: Int): Contact

    // Medication
    @GET("medication")
    suspend fun getMedication(): List<Medication>
    @GET("medication/today")
    suspend fun getMedicationForToday(): List<Medication>
    @GET("medication/future")
    suspend fun getMedicationForFuture(): List<Medication>
    @PUT("medication/today/taken")
    suspend fun toggleAllMedicationForTodayAsTaken(): List<Medication>
    @PUT("medication/{id}/taken")
    suspend fun toggleMedicationAsTaken(@Path("id") id: Int): List<Medication>

    // Tasks
    @GET("tasks-today")
    suspend fun getTasksForToday(): List<Task>
    @GET("tasks-future")
    suspend fun getTasksForFuture(): List<Task>
    @GET("tasks/{id}")
    suspend fun getTaskWithId(@Path("id") id: Int): Task
    @PUT("tasks/{id}")
    suspend fun updateTaskDate(@Path("id") id: Int, @Body request: TaskUpdateRequest): List<Task>
    data class TaskUpdateRequest(val date: String)

    @GET("tasks/{id}/shoppinglist")
    suspend fun getTaskShoppingListItems(@Path("id") id: Int): List<ShoppingItems>

    @PUT("shoppinglists/{id}/items/{productId}/decrease")
    suspend fun decreaseProductAmount(@Path("id") id: Int, @Path("productId") productId: Int): List<ShoppingItems>
    @PUT("shoppinglists/{id}/items/{productId}/increase")
    suspend fun increaseProductAmount(@Path("id") id: Int, @Path("productId") productId: Int): List<ShoppingItems>
    @DELETE("shoppinglists/{id}/items/{productId}")
    suspend fun removeProduct(@Path("id") id: Int, @Path("productId") productId: Int): List<ShoppingItems>
    @POST("shoppinglists/{id}/items")
    suspend fun addProduct(@Path("id") id: Int, @Body request: AddProductRequest): List<ShoppingItems>
    data class AddProductRequest(val name: String, val description: String, val image: String, val quantity: Int = 1)
}

object CCApi {
    val retrofitService: CareConnectAPIService by lazy {
        retrofit.create(CareConnectAPIService::class.java)
    }
}
