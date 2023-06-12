package be.howest.tuurdelacroix.careconnect.models.local


data class Patient (
    override val firstName: String,
    override val lastName: String,
    override val profilePic: Int?,
    override val phoneNumber: String,
    val headCarer: User,
    var contacts: List<Contact>,
    var medication: List<Medication>,
    var tasksForToday: List<Task>,
    var tasksForFuture: List<Task>
) : User(firstName, lastName, profilePic, UserType.PATIENT, phoneNumber) {

}