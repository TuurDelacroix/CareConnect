<?php

namespace App\Modules\Services;

use App\Models\ShoppingItem;
use App\Models\ShoppingList;
use App\Models\Task;
use Carbon\Carbon;
use Illuminate\Support\Facades\Validator;

class TaskShoppingListService extends Service
{
    protected $add_product_rules = [
        'name' => 'required|string',
        'description' => 'nullable|string',
        'image' => 'nullable|string',
        'quantity' => 'integer|min:1',
    ];

    public function __construct(ShoppingList $model)
    {
        parent::__construct($model);
    }

    public function getShoppingLists()
    {
        $shoppingLists = ShoppingList::with('shoppingItems')->get();
        return response()->json($shoppingLists);
    }

    public function getShoppingListById($id)
    {
        $shoppingList = ShoppingList::with('shoppingItems')->find($id);

        if (!$shoppingList) {
            return response()->json(['message' => 'Shopping list not found.'], 404);
        }

        return response()->json($shoppingList);
    }

    public function getShoppingListItemsById($id)
    {
        $shoppingList = ShoppingList::with('shoppingItems')->findOrFail($id);
        return response()->json($shoppingList->shoppingItems);
    }

    public function getShoppingListItemsByTaskId($id)
    {
        $task = Task::with('shoppingList.shoppingItems')->findOrFail($id);
        $shoppingList = $task->shoppingList;

        if (!$shoppingList) {
            return response()->json(['message' => 'No shopping list found for the given task id.'], 404);
        }

        return response()->json($shoppingList->shoppingItems);
    }

    public function addProduct($data, $shoppingListId)
    {
        $validator = Validator::make($data, $this->add_product_rules);

        if ($validator->fails()) {
            return response()->json(['errors' => $validator->errors()], 400);
        }

        $shoppingList = ShoppingList::findOrFail($shoppingListId);

        $shoppingItem = new ShoppingItem([
            'name' => $data['name'],
            'description' => $data['description'] ?? null,
            'image' => $data['image'] ?? null,
            'quantity' => $data['quantity'] ?? 1,
        ]);

        $shoppingList->shoppingItems()->save($shoppingItem, ['shopping_items' => true]);

        return response()->json($shoppingList->shoppingItems);
    }

    public function decreaseQuantity($data, $shoppingListId, $productId)
    {
        $shoppingList = ShoppingList::findOrFail($shoppingListId);
        $shoppingItem = $shoppingList->shoppingItems()->where('id', $productId)->firstOrFail();

        $shoppingItem->decrement('quantity');

        return response()->json($shoppingList->shoppingItems);
    }

    public function increaseQuantity($data, $shoppingListId, $productId)
    {
        $shoppingList = ShoppingList::findOrFail($shoppingListId);
        $shoppingItem = $shoppingList->shoppingItems()->where('id', $productId)->firstOrFail();

        $shoppingItem->increment('quantity');

        return response()->json($shoppingList->shoppingItems);
    }

    public function removeProduct($data, $shoppingListId, $productId)
    {
        $shoppingList = ShoppingList::findOrFail($shoppingListId);
        $shoppingItem = $shoppingList->shoppingItems()->where('id', $productId)->firstOrFail();

        $shoppingItem->delete();

        return response()->json($shoppingList->shoppingItems);
    }
}