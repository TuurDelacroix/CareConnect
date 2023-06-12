package be.howest.tuurdelacroix.careconnect.models.local

open class User(
    open val firstName: String,
    open val lastName: String,
    open val profilePic: Int?,
    open val profileType: UserType,
    open val phoneNumber: String,
) {

    fun getFullName(): String {
        return "$firstName $lastName"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (phoneNumber != other.phoneNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        return result
    }
}

