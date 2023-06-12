package be.howest.tuurdelacroix.careconnect.ui.screens

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import be.howest.tuurdelacroix.careconnect.R
import be.howest.tuurdelacroix.careconnect.composables.ActionButton
import be.howest.tuurdelacroix.careconnect.composables.BackButton
import be.howest.tuurdelacroix.careconnect.composables.ErrorScreen
import be.howest.tuurdelacroix.careconnect.composables.LoadingScreen
import be.howest.tuurdelacroix.careconnect.composables.PageTitleCard
import be.howest.tuurdelacroix.careconnect.data.CCUiState
import be.howest.tuurdelacroix.careconnect.localroom.CCDatabase
import be.howest.tuurdelacroix.careconnect.localroom.ShoppingCardImage
import be.howest.tuurdelacroix.careconnect.models.api.ShoppingItems
import be.howest.tuurdelacroix.careconnect.network.CCApi
import be.howest.tuurdelacroix.careconnect.network.CareConnectAPIService
import be.howest.tuurdelacroix.careconnect.ui.CCAPIUiState
import be.howest.tuurdelacroix.careconnect.ui.CCViewModel
import coil.compose.rememberImagePainter
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.ListSolid
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

lateinit var outputDir: File
private lateinit var cameraExecutor: ExecutorService

private var showCamera: MutableState<Boolean> = mutableStateOf(false)
private var madePicture: MutableState<Boolean> = mutableStateOf(false)

private var pictureUri: Uri? = null

private fun handleImageCapture(uri: Uri, viewModel: CCViewModel)
{
    Log.d("Camera","Image captured: $uri")
    pictureUri = uri
    showCamera.value = false
}

// Function to reset the captured image
fun resetImage() {
    pictureUri = null
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddProductScreen(
    viewModel: CCViewModel,
    uiState: CCUiState,
    navController: NavHostController,
    modifier: Modifier,
    localRoomDb: CCDatabase
) {
    cameraExecutor = Executors.newSingleThreadExecutor()

    val ccAPIUiState = viewModel.ccAPIUiState;
    when (ccAPIUiState) {
        is CCAPIUiState.Loading -> LoadingScreen(ccAPIUiState is CCAPIUiState.Success)
        is CCAPIUiState.Error -> ErrorScreen("Er heeft zich een fout voorgedaan. Probeer later opnieuw.", LocalContext.current)
        is CCAPIUiState.Success -> {


            if (showCamera.value) {
                CameraView(
                    outputDirectory = outputDir,
                    executor = cameraExecutor,
                    onImageCaptured = { uri: Uri -> handleImageCapture(uri, viewModel) },
                    onError = { Log.e("Camera", "CameraView Error:", it) },
                    showCamera = showCamera,
                    madePicture = madePicture
                )
            } else {
                Column(
                    modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(MaterialTheme.colors.background),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    BackButton { navController.navigate(CareConnectScreens.ShoppingList.name) }

                    PageTitleCard(
                        icon = LineAwesomeIcons.ListSolid,
                        stringResource(
                            R.string.cc_addproduct_screen_title
                        )
                    )

                    AdditionForm(ccAPIUiState, viewModel, modifier, navController, showCamera, pictureUri, localRoomDb)

                }
            }

        }
    }
}

private var productNameState = mutableStateOf("")
private var productDescriptionState = mutableStateOf("")

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AdditionForm(
    uiState: CCAPIUiState.Success,
    viewModel: CCViewModel,
    modifier: Modifier,
    navController: NavHostController,
    showCamera: MutableState<Boolean>,
    pictureUri: Uri?,
    localRoomDb: CCDatabase
) {

    val context = LocalContext.current

    var productName by productNameState
    var productDescription by productDescriptionState
    val capturedImageUri by remember { mutableStateOf(pictureUri) }
    val isImageCaptured = pictureUri != null

    val keyboard = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = productName,
            onValueChange = { productNameState.value = it }, // Update the mutable state
            label = { Text(stringResource(R.string.cc_addproduct_form_productname_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboard?.hide()
                }
            )
        )
        OutlinedTextField(
            value = productDescription,
            onValueChange = { productDescriptionState.value = it }, // Update the mutable state
            label = { Text(text = stringResource(R.string.cc_addproduct_form_productdesc_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboard?.hide()
                }
            )
        )
        if (isImageCaptured) {
            Image(
                painter = rememberImagePainter(capturedImageUri),
                contentDescription = "Product Image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 16.dp)
            )
        }

        ActionButton(
            onClickAction = { /*TODO*/
                showCamera.value = true
            },
            buttonColor = R.color.light_green,
            buttonTextColor = MaterialTheme.colors.secondary,
            buttonText = R.string.cc_taskedit_shopl_make_picture,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        ActionButton(
            onClickAction = {
//                val product = ShoppingItem(
//                    name = productName,
//                    description = productDescription,
//                    //image = productImageUri
//                    image = ""
//                )
                //UserRepo.addProduct(uiState, product, context)
                viewModel.viewModelScope.launch {

                    try {
                        var updatedShoppingItems: List<ShoppingItems>
                        if (pictureUri != null)
                        {
                             updatedShoppingItems = CCApi.retrofitService.addProduct(viewModel.selectedTask?.shoppingList?.id!!,
                                CareConnectAPIService.AddProductRequest(productName, productDescription, pictureUri.toString())
                            )
                        }
                        else
                        {
                            updatedShoppingItems = CCApi.retrofitService.addProduct(viewModel.selectedTask?.shoppingList?.id!!,
                                CareConnectAPIService.AddProductRequest(productName, productDescription, "https://www.kuleuven.be/communicatie/congresbureau/fotos-en-afbeeldingen/no-image.png/image"))
                        }

                        viewModel.shoppingListOfSelectedTask = updatedShoppingItems
                        viewModel.selectedTask = CCApi.retrofitService.getTaskWithId(viewModel.selectedTask?.id!!)

                        // Add image to localroom db
                        val productImage = ShoppingCardImage(
                            shoppingListId = viewModel.selectedTask?.shoppingList?.id!!,
                            productName = productName, uri = pictureUri.toString()
                        )
                        localRoomDb.ShoppingCardImageDao().insertProductImage(productImage)

                        navController.navigate(CareConnectScreens.ShoppingList.name)
                        productName = ""
                        productDescription = ""
                        resetImage();

                    } catch (ex: Exception)
                    {
                        Log.e("CC", ex.toString())
                    }

                }

            },
            buttonColor = R.color.light_green,
            buttonTextColor = MaterialTheme.colors.secondary,
            buttonText = R.string.cc_taskedit_shopl_addproduct_button_text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}