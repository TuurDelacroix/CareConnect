<?php

namespace App\Modules\Services;

use App\Models\Patient;
use App\Models\ShoppingItem;
use App\Models\ShoppingList;
use App\Models\Task;
use App\Models\TaskStatus;
use App\Models\TaskType;
use Illuminate\Support\Facades\Validator;
use Carbon\Carbon;

class TaskService extends Service
{
    protected $add_rules = [
        'title' => 'required|string',
        'type' => "required|string|in:".TaskType::DO_SHOPPING.",".TaskType::ACCOMPANY,
        'status' => "string|required|in:".TaskStatus::REQUESTED.",".TaskStatus::IN_PROGRESS.",".TaskStatus::COMPLETED,
        'date' => 'required|date',
        'shoppinglist' => "nullable|required_if:type,".TaskType::DO_SHOPPING,
        'shoppinglist.name' => 'string|nullable',
        'shoppinglist.shopping_items' => 'array|nullable',
        'shoppinglist.shopping_items.*.name' => 'string|required',
        'shoppinglist.shopping_items.*.description' => 'string|nullable',
        'shoppinglist.shopping_items.*.image' => 'string|nullable',
        'shoppinglist.shopping_items.*.quantity' => 'integer|required',
    ];

    protected $update_rules = [
        'date' => 'required|date'
    ];

    public function __construct(Task $model)
    {
        parent::__construct($model);
    }

    public function getTasks()
    {
        $tasks = Task::with('shoppingList.shoppingItems')->orderBy('date')->orderByRaw("FIELD(status, 'COMPLETED', 'IN_PROGRESS', 'REQUESTED')")->get();

        foreach ($tasks as $task) {
            $task->date = Carbon::parse($task->date)->toDateTimeString();
        }
        
        return response()->json($tasks);
    }

    public function getTasksForToday()
    {
        $tasks = Task::whereRaw('DATE(date) = DATE(NOW())')->with('shoppingList.shoppingItems')->orderBy('date')->orderByRaw("FIELD(status, 'COMPLETED', 'IN_PROGRESS', 'REQUESTED')")->get();
        
        foreach ($tasks as $task) {
            $task->date = Carbon::parse($task->date)->toDateTimeString();
        }
        
        return response()->json($tasks);
    }
    
    public function getTasksForFuture()
    {
        $tasks = Task::whereRaw('DATE(date) > DATE(NOW())')->with('shoppingList.shoppingItems')->orderBy('date')->orderByRaw("FIELD(status, 'COMPLETED', 'IN_PROGRESS', 'REQUESTED')")->get();
        
        foreach ($tasks as $task) {
            $task->date = Carbon::parse($task->date)->toDateTimeString();
        }

        return response()->json($tasks);
    }
    
    public function getTasksWithId($id)
    {
        $task = Task::where('id', $id)->with('shoppingList.shoppingItems')->orderBy('date')->orderByRaw("FIELD(status, 'COMPLETED', 'IN_PROGRESS', 'REQUESTED')")->first();

        if (!$task) {
            return response()->json(['message' => 'No task found with the given id.'], 404);
        }

        $task->date = Carbon::parse($task->date)->toDateTimeString();

        return response()->json($task);
    }

    public function addTask($data)
    {
        $validator = Validator::make($data, $this->add_rules);

        if ($validator->fails()) {
            return response()->json(['errors' => $validator->errors()], 400);
        }

        // Create the task
        $task = new Task([
            'title' => $data['title'],
            'type' => $data['type'],
            'status' => $data['status'],
            'date' => $data['date'],
        ]);

        // If the task type is DO_SHOPPING, create the shopping list and attach it to the task
        
        if ($data['type'] === TaskType::DO_SHOPPING)
        {
            $shoppingList = new ShoppingList([
                'name' => $data['shoppinglist']['name']
            ]);

            $shoppingList->save();

            $task->shoppingList()->associate($shoppingList);

            // If there are shopping list items, create them and attach them to the shopping list using the pivot table
            $shoppingItems = $data['shoppinglist']['shopping_items'];

            if (!empty($shoppingItems))
            {
                foreach ($shoppingItems as $item)
                {
                    $shoppingItem = new ShoppingItem([
                        'name' => $item['name'],
                        'description' => $item['description'] ?? null,
                        'image' => $item['image'] ?? null,
                        'quantity' => $item['quantity'] ?? 1,
                    ]);

                    $shoppingList->shoppingItems()->save($shoppingItem, ['shopping_items' => true]);
                }
            }
        }

        $task->save();

        $patient = Patient::findOrFail(1);
        $patient->tasks()->attach($task->id);

        return $task;

    }

    public function changeDateOfTask($id, $data)
    {
        $validator = Validator::make($data, $this->update_rules);

        if ($validator->fails()) {
            return response()->json(['errors' => $validator->errors()], 400);
        }

        $task = Task::findOrFail($id);
        $task->date = Carbon::parse($data['date']);
        $task->save();

        return Task::whereRaw('DATE(date) > DATE(NOW())')->with('shoppingList.shoppingItems')->orderBy('date')->orderByRaw("FIELD(status, 'COMPLETED', 'IN_PROGRESS', 'REQUESTED')")->get();
    }
}