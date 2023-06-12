<?php

use App\Http\Controllers\CCAppController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});

Route::get('/current-user', [CCAppController::class, 'getCurrentAppUser']);
Route::get('/current-user-full', [CCAppController::class, 'getCurrentAppUserFull']);

Route::get('/medication', [CCAppController::class, 'getMedication']);
Route::get('/medication/today', [CCAppController::class, 'getMedicationForToday']);
Route::get('/medication/future', [CCAppController::class, 'getMedicationForFuture']);

Route::post('/medication', [CCAppController::class, 'addMedication']);

Route::put('/medication/{id}/taken', [CCAppController::class, 'markMedicationAsTaken'])->where('id', '[0-9]+');;
Route::put('/medication/today/taken', [CCAppController::class, 'markAllMedicationForTodayAsTaken']);


Route::get('/headcarer', [CCAppController::class, 'getHeadcarer']);


Route::get('/tasks', [CCAppController::class, "getTasks"]);
Route::get('/tasks/{id}', [CCAppController::class, "getTasksWithId"])->where('id', '[0-9]+');
Route::get('/tasks-today', [CCAppController::class, "getTasksForToday"]);
Route::get('/tasks-future', [CCAppController::class, "getTasksForFuture"]);

Route::post('/tasks', [CCAppController::class, "addTask"]);

Route::put('/tasks/{id}', [CCAppController::class, "changeDateOfTask"])->where('id', '[0-9]+');
Route::get('/tasks/{id}/shoppinglist', [CCAppController::class, "getShoppingListItemsByTaskId"])->where('id', '[0-9]+');


Route::get('/shoppinglists', [CCAppController::class, "getShoppingLists"]);
Route::get('/shoppinglists/{id}', [CCAppController::class, "getShoppingListById"])->where('id', '[0-9]+');
Route::get('/shoppinglists/{id}/items', [CCAppController::class, "getShoppingListItemsById"])->where('id', '[0-9]+');

Route::post('/shoppinglists/{id}/items', [CCAppController::class, "addProduct"]);
Route::put('/shoppinglists/{id}/items/{productId}/decrease', [CCAppController::class, "decreaseQuantity"])->where(['id' => '[0-9]+', 'productId' => '[0-9]+']);
Route::put('/shoppinglists/{id}/items/{productId}/increase', [CCAppController::class, "increaseQuantity"])->where(['id' => '[0-9]+', 'productId' => '[0-9]+']);
Route::delete('/shoppinglists/{id}/items/{productId}', [CCAppController::class, "removeProduct"])->where(['id' => '[0-9]+', 'productId' => '[0-9]+']);

Route::get('/events', [CCAppController::class, "getEvents"]);
Route::get('/events/today', [CCAppController::class, "getEventsForToday"]);

Route::post('/events', [CCAppController::class, "addEvent"]);

Route::get('/contacts', [CCAppController::class, "getContacts"]);

Route::post('/contacts', [CCAppController::class, "addContact"]);
Route::put('/contacts/{id}/toggle-reachable', [CCAppController::class, "toggleReachable"]);
