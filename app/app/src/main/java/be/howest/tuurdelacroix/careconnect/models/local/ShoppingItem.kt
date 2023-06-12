package be.howest.tuurdelacroix.careconnect.models.local

//todo replace internet picture with uploadable picture (not needed, when there is no picture placeholder will be shown (NEED DRAWABLE)
data class ShoppingItem(val name: String, val description: String?, val image: String?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShoppingItem

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
