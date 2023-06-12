package be.howest.tuurdelacroix.careconnect.localroom

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Database(entities = [ShoppingCardImage::class], version = 3)
abstract class CCDatabase : RoomDatabase() {
    abstract fun ShoppingCardImageDao(): ShoppingCardImageDao
}

@Entity
data class ShoppingCardImage(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "related_shoppinglist") val shoppingListId: Int,
    @ColumnInfo(name = "productName") val productName: String,
    @ColumnInfo(name = "picture_uri") val uri: String?
)

@Dao
interface ShoppingCardImageDao {

    @Query("SELECT * FROM ShoppingCardImage")
    suspend fun getAllItems() : List<ShoppingCardImage>

    @Query("SELECT picture_uri FROM ShoppingCardImage WHERE related_shoppinglist = :shoppingListId AND productName = :productName AND picture_uri = :uri")
    suspend fun getProductImageUri(shoppingListId: Int, productName: String, uri: String?): String?

    @Insert
    suspend fun insertProductImage(shoppingCardImage: ShoppingCardImage)

    @Query("DELETE FROM ShoppingCardImage WHERE related_shoppinglist = :shoppingListId AND productName = :productName AND picture_uri = :uri")
    suspend fun deleteProductImage(shoppingListId: Int, productName: String, uri: String?)
}

