package be.howest.tuurdelacroix.careconnect.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import be.howest.tuurdelacroix.careconnect.R
import be.howest.tuurdelacroix.careconnect.composables.ActionButton
import be.howest.tuurdelacroix.careconnect.composables.BackButton
import be.howest.tuurdelacroix.careconnect.composables.ErrorScreen
import be.howest.tuurdelacroix.careconnect.composables.LoadingScreen
import be.howest.tuurdelacroix.careconnect.composables.NoContentToShow
import be.howest.tuurdelacroix.careconnect.composables.PageTitleCard
import be.howest.tuurdelacroix.careconnect.composables.YesNoPopup
import be.howest.tuurdelacroix.careconnect.data.CCUiState
import be.howest.tuurdelacroix.careconnect.data.showToastAlert
import be.howest.tuurdelacroix.careconnect.localroom.CCDatabase
import be.howest.tuurdelacroix.careconnect.models.api.ShoppingItems
import be.howest.tuurdelacroix.careconnect.network.CCApi
import be.howest.tuurdelacroix.careconnect.ui.CCAPIUiState
import be.howest.tuurdelacroix.careconnect.ui.CCViewModel
import coil.compose.rememberImagePainter
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.ListSolid
import compose.icons.lineawesomeicons.MinusSolid
import compose.icons.lineawesomeicons.PlusSolid
import compose.icons.lineawesomeicons.TrashSolid
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShoppingListScreen(
    viewModel: CCViewModel,
    uiState: CCUiState,
    navController: NavHostController,
    modifier: Modifier,
    localRoomDb: CCDatabase
) {
    val ccAPIUiState = viewModel.ccAPIUiState
    when (ccAPIUiState) {
        is CCAPIUiState.Loading -> LoadingScreen(ccAPIUiState is CCAPIUiState.Success)
        is CCAPIUiState.Error -> ErrorScreen("Er heeft zich een fout voorgedaan. Probeer later opnieuw.", LocalContext.current)
        is CCAPIUiState.Success -> {
            Column(
                modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(MaterialTheme.colors.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BackButton { navController.navigate(CareConnectScreens.Edit.name) }

                PageTitleCard(
                    icon = LineAwesomeIcons.ListSolid,
                    stringResource(
                        R.string.cc_edit_shopl_screen_title,
                        viewModel.selectedTask?.title ?: ""
                    )
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (viewModel.shoppingListOfSelectedTask.isNullOrEmpty()) {
                        NoContentToShow(
                            noContentText = R.string.cc_shoppinglist_no_items_text,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        ShoppingList(
                            ccAPIUiState,
                            viewModel,
                            modifier = Modifier.fillMaxSize(),
                            localRoomDb
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionButton(
                        onClickAction = {
                            navController.navigate(CareConnectScreens.AddProduct.name)
                        },
                        buttonColor = R.color.light_green,
                        buttonTextColor = MaterialTheme.colors.secondary,
                        buttonText = R.string.cc_taskedit_shopl_addproduct_button_text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )

                    ActionButton(
                        onClickAction = {
                            //todo save edited list
                            // not a real functionality, just navigating
                            Log.d("CC", "save shopping list")
                            navController.navigate(CareConnectScreens.Edit.name)
                        },
                        buttonColor = R.color.light_green,
                        buttonTextColor = MaterialTheme.colors.secondary,
                        buttonText = R.string.cc_taskedit_shopl_save_text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShoppingList(
    uiState: CCAPIUiState.Success,
    viewModel: CCViewModel,
    modifier: Modifier,
    localRoomDb: CCDatabase
)
{
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    //var shoppingList by remember { mutableStateOf(uiState.shoppingListOfSelectedTask) }
    var shoppingList by remember { mutableStateOf(viewModel.shoppingListOfSelectedTask) }

    var totalAmount = shoppingList?.sumOf { it.quantity!! } ?: 0

    LazyColumn {

        item {
            Text(
                text = "Totaal aantal: $totalAmount".uppercase(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        itemsIndexed(shoppingList ?: emptyList()) { index, product ->

            ProductView(
                product = product,
                amount = product.quantity!!,
                onIncreaseProduct = {
                    //UserRepo.increaseProductAmount(uiState, product)
                    //shoppingList = uiState.shoppingListOfSelectedTask
                    viewModel.viewModelScope.launch {
                        try {

                            val updatedShoppingItems = CCApi.retrofitService.increaseProductAmount(product.shoppingListId!!, product.id!!)
                            viewModel.shoppingListOfSelectedTask = updatedShoppingItems
                            viewModel.selectedTask = CCApi.retrofitService.getTaskWithId(viewModel.selectedTask?.id!!)
                            shoppingList = viewModel.shoppingListOfSelectedTask

                        } catch (ex: Exception)
                        {
                            Log.e("CC", ex.toString())
                        }
                    }
                },
                onDecreaseProduct = {
                    //UserRepo.decreaseProductAmount(uiState, product)
                    //shoppingList = uiState.shoppingListOfSelectedTask
                    viewModel.viewModelScope.launch {
                        try {

                            if (product.quantity!! > 1)
                            {
                                val updatedShoppingItems = CCApi.retrofitService.decreaseProductAmount(product.shoppingListId!!, product.id!!)
                                viewModel.shoppingListOfSelectedTask = updatedShoppingItems
                                viewModel.selectedTask = CCApi.retrofitService.getTaskWithId(viewModel.selectedTask?.id!!)
                                shoppingList = viewModel.shoppingListOfSelectedTask
                            }
                            else
                            {
                                showToastAlert(context, "Minimum bereikt")
                            }

                        } catch (ex: Exception)
                        {
                            Log.e("CC", ex.toString())
                        }
                    }
                },
                onRemoveProduct = {
                    showDialog = true
                    //UserRepo.removeProduct(uiState, product, context)
                    //shoppingList = uiState.shoppingListOfSelectedTask
                },
                localRoomDb,
                viewModel
            )

            if (showDialog)
            {
                YesNoPopup(
                    stringResource(R.string.cc_alert_shoppinglist_remove_product),
                    stringResource(R.string.cc_alert_shoppinglist_remove_product_text, product.name!!),
                    {
//                        UserRepo.removeProduct(uiState, productToRemove, context)
//                        shoppingList = uiState.shoppingListOfSelectedTask
                        viewModel.viewModelScope.launch {
                            try {

                                val updatedShoppingItems = CCApi.retrofitService.removeProduct(product.shoppingListId!!, product.id!!)
                                viewModel.shoppingListOfSelectedTask = updatedShoppingItems
                                viewModel.selectedTask = CCApi.retrofitService.getTaskWithId(viewModel.selectedTask?.id!!)
                                shoppingList = viewModel.shoppingListOfSelectedTask
                            } catch (ex: Exception)
                            {
                                Log.e("CC", ex.toString())
                            }
                        }

                        showDialog = false
                    },
                    {showDialog = false},
                    {showDialog = false})
            }
        }
    }

}



@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProductView(
    product: ShoppingItems,
    amount: Int,
    onIncreaseProduct: () -> Unit,
    onDecreaseProduct: () -> Unit,
    onRemoveProduct: () -> Unit,
    localRoomDb: CCDatabase,
    viewModel: CCViewModel
) {
    var imageToShow by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val productImageUri = localRoomDb.ShoppingCardImageDao().getProductImageUri(viewModel.selectedTask!!.shoppingList!!.id!!, product.name!!, product.image)
        Log.d("ImageURI", "${product.name!!} & ${viewModel.selectedTask!!.shoppingList!!.id!!}")
        if (productImageUri != null) {
            Log.d("ImageURI", productImageUri)
        } else {
            Log.d("ImageURI", "Product image URI is null")
        }

        imageToShow = productImageUri ?: product.image ?: "https://cdn.onlinewebfonts.com/svg/img_546302.png"
        Log.d("ImageDefault", imageToShow)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colors.secondary),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Image(
                painter = rememberImagePainter(
                    data = imageToShow,
                    builder = {
                        crossfade(true)
                        //placeholder(R.drawlable.???)
                    }
                ),
                contentDescription = product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = product.name!!, fontWeight = FontWeight.Bold)
                if (product.description != null) {
                    Text(
                        text = product.description!!,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IconButton(
                    onClick = onIncreaseProduct,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = LineAwesomeIcons.PlusSolid,
                        contentDescription = "Increase Product Count"
                    )
                }

                Text(
                    text = amount.toString(),
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onDecreaseProduct,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = LineAwesomeIcons.MinusSolid,
                        contentDescription = "Decrease Product Count"
                    )
                }
            }

            IconButton(
                onClick = {
                    onRemoveProduct()
                    viewModel.viewModelScope.launch {
                        localRoomDb.ShoppingCardImageDao().deleteProductImage(viewModel.selectedTask!!.shoppingList!!.id!!, product.name!!, product.image)
                    }
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = LineAwesomeIcons.TrashSolid,
                    contentDescription = "Remove product"
                )
            }
        }
    }



}