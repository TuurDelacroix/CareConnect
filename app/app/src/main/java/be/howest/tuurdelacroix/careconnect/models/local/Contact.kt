package be.howest.tuurdelacroix.careconnect.models.local

data class Contact(
    override val firstName: String,
    override val lastName: String,
    override val profilePic: Int?,
    override val phoneNumber: String,
    var reachable: Boolean = false
) : User(firstName, lastName, profilePic, UserType.CONTACT, phoneNumber) {

}
