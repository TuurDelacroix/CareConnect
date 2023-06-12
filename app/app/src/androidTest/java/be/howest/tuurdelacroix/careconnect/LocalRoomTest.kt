package be.howest.tuurdelacroix.careconnect.composables

import android.content.Context
import androidx.compose.ui.test.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.howest.tuurdelacroix.careconnect.localroom.CCDatabase
import be.howest.tuurdelacroix.careconnect.localroom.ShoppingCardImage
import be.howest.tuurdelacroix.careconnect.localroom.ShoppingCardImageDao
import be.howest.tuurdelacroix.careconnect.models.api.ShoppingItems
import be.howest.tuurdelacroix.careconnect.models.api.ShoppingList
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ShoppingCardImageDaoTest {
    private lateinit var shoppingCardImageDao: ShoppingCardImageDao
    private lateinit var ccDatabase: CCDatabase
    private var shopl = ShoppingList(1, "TestCart", arrayListOf(
        ShoppingItems(1, "TestItem1", "For test purposes", "https://media.istockphoto.com/id/157314260/photo/perfect-grandma-xxl.jpg?s=612x612&w=0&k=20&c=Y7K-Yo-lGUp43oYKa9bW9gTsrBcS2Raasoo3L1dvDnI=", 1, 1),
        ShoppingItems(0, "TestItem2", "For test purposes", "https://media.istockphoto.com/id/157314260/photo/perfect-grandma-xxl.jpg?s=612x612&w=0&k=20&c=Y7K-Yo-lGUp43oYKa9bW9gTsrBcS2Raasoo3L1dvDnI=", 1, 1)
    )
    )

    private var shopl_1_img = ShoppingCardImage(1, shopl.shoppingItems[0].id!!, shopl.shoppingItems[0].name!!, shopl.shoppingItems[0].image!!)
    private var shopl_2_img = ShoppingCardImage(2, shopl.shoppingItems[1].id!!, shopl.shoppingItems[1].name!!, shopl.shoppingItems[1].image!!)


    // Open database connection
    @Before
    fun createDb()
    {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Use in-memory db (after this process information stored will disappear)
        ccDatabase = Room.inMemoryDatabaseBuilder(context, CCDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        shoppingCardImageDao = ccDatabase.ShoppingCardImageDao()
    }

    private suspend fun addOneItemToDb()
    {
        shoppingCardImageDao.insertProductImage(shopl_1_img)
    }

    private suspend fun addTwoItemsToDb()
    {
        shoppingCardImageDao.insertProductImage(shopl_1_img)
        shoppingCardImageDao.insertProductImage(shopl_2_img)
    }

    private suspend fun removeFirstItemFromDb()
    {
        shoppingCardImageDao.deleteProductImage(shopl.shoppingItems[0].shoppingListId!!, shopl.shoppingItems[0].name!!, shopl.shoppingItems[0].image!!)
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsItemIntoDb() = runBlocking {
        addOneItemToDb()
        val allItems = shoppingCardImageDao.getAllItems()
        TestCase.assertEquals(allItems[0], shopl_1_img)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetProductImageUri_returnsCorrectUri() = runBlocking {
        addOneItemToDb()
        val uri = shoppingCardImageDao.getProductImageUri(shopl.shoppingItems[0].shoppingListId!!, shopl.shoppingItems[0].name!!, shopl.shoppingItems[0].image!!)
        TestCase.assertEquals(shopl.shoppingItems[0].image, uri)
    }

    @Test
    @Throws(Exception::class)
    fun daoDeleteProductImage_removesItemFromDb() = runBlocking {
        addTwoItemsToDb()
        removeFirstItemFromDb()
        val allItems = shoppingCardImageDao.getAllItems()
        TestCase.assertEquals(1, allItems.size)
        TestCase.assertEquals(shopl_2_img, allItems[0])
    }


    // Close database connection
    @After
    @Throws(IOException::class)
    fun closeDb()
    {
        ccDatabase.close()
    }
}