<?php

namespace App\Http\Controllers;

use App\Models\Contact;
use App\Models\Event;
use App\Models\Headcarer;
use App\Models\Medication;
use App\Models\Patient;
use App\Models\ShoppingItem;
use App\Models\ShoppingList;
use App\Models\Task;
use App\Models\User;
use App\Modules\Services\ContactService;
use App\Modules\Services\EventService;
use App\Modules\Services\HeadcarerService;
use App\Modules\Services\MedicationService;
use App\Modules\Services\TaskService;
use App\Modules\Services\TaskShoppingListService;
use App\Modules\Services\UserService;
use Illuminate\Http\Request;

class CCAppController extends Controller
{
    private $_userService;
    private $_medService;
    private $_hcService;
    private $_tskService;
    private $_eventService;
    private $_cntctService;

    public function __construct(UserService $userService, MedicationService $medService, HeadcarerService $hcService, TaskService $tskService, TaskShoppingListService $shoplService, EventService $eventService, ContactService $cntctService)
    {
        $this->_userService = $userService;
        $this->_medService = $medService;
        $this->_hcService = $hcService;
        $this->_tskService = $tskService;
        $this->_shoplService = $shoplService;
        $this->_eventService = $eventService;
        $this->_cntctService = $cntctService;
    }


    public function getCurrentAppUser(Patient $model, Request $request)
    {
        $user = $this->_userService->getCurrentAppUser();

        return $user;
    }

    public function getCurrentAppUserFull(Patient $model, Request $request)
    {
        $user = $this->_userService->getCurrentAppUserFull();

        return $user;
    }

    public function getHeadcarer(Headcarer $model, Request $request)
    {
        $headcarer = $this->_hcService->getHeadcarer();

        return $headcarer;
    }

    
    // MEDICATION
    // GET
    public function getMedication(Medication $model, Request $request)
    {
        $medication = $this->_medService->getMedication();

        return $medication;
    }

    public function getMedicationForToday(Medication $model, Request $request)
    {
        $medication = $this->_medService->getMedicationForToday();

        return $medication;
    }

    public function getMedicationForFuture(Medication $model, Request $request)
    {
        $medication = $this->_medService->getMedicationForFuture();

        return $medication;
    }

    // POST
    public function addMedication(Request $request)
    {
        $data = $request->all();
        $medication = $this->_medService->addMedication($data);

        if ($this->_medService->hasErrors())
        {
            return ["errors" => $this->_medService->getErrors()];
        }

        return $medication;
    }

    // PUT
    public function markMedicationAsTaken(Request $request)
    {
        $id = $request->id;
        $updatedMedicationList = $this->_medService->markMedicationAsTaken($id);

        if ($this->_medService->hasErrors())
        {
            return ["errors" => $this->_medService->getErrors()];
        }

        return $updatedMedicationList;
    }

    public function markAllMedicationForTodayAsTaken(Request $request)
    {
        $updatedMedicationList = $this->_medService->markAllMedicationForTodayAsTaken();
        
        if ($this->_medService->hasErrors())
        {
            return ["errors" => $this->_medService->getErrors()];
        }

        return $updatedMedicationList;
    }
    

    // TASKS
    // GET
    public function getTasks(Task $model, Request $request)
    {
        $tasks = $this->_tskService->getTasks();

        return $tasks;
    }

    public function getTasksForToday(Task $model, Request $request)
    {
        $tasks = $this->_tskService->getTasksForToday();

        return $tasks;
    }

    public function getTasksForFuture(Task $model, Request $request)
    {
        $tasks = $this->_tskService->getTasksForFuture();

        return $tasks;
    }

    public function getTasksWithId(Task $model, Request $request)
    {
        $id = $request->id;
        $shoppingLists = $this->_tskService->getTasksWithId($id);

        return $shoppingLists;
    }

    // POST
    public function addTask(Task $model, Request $request)
    {
        $data = $request->all();
        $task = $this->_tskService->addTask($data);

        if ($this->_tskService->hasErrors())
        {
            return ["errors" => $this->_tskService->getErrors()];
        }

        return $task;
    }

    // PUT
    public function changeDateOfTask(Task $model, Request $request)
    {
        $id = $request->id;
        $data = $request->all();
        $updatedTaskList = $this->_tskService->changeDateOfTask($id, $data);

        if ($this->_tskService->hasErrors())
        {
            return ["errors" => $this->_tskService->getErrors()];
        }

        return $updatedTaskList;
    }

    // SHOPPINGLISTS FOR TASK
    // GET
    public function getShoppingLists(Task $model, Request $request)
    {
        $shoppingLists = $this->_shoplService->getShoppingLists();

        return $shoppingLists;
    }

    public function getShoppingListById(Task $model, Request $request)
    {
        $id = $request->id;
        $shoppingLists = $this->_shoplService->getShoppingListById($id);

        return $shoppingLists;
    }

    public function getShoppingListItemsById(Task $model, Request $request)
    {
        $id = $request->id;
        $items = $this->_shoplService->getShoppingListItemsById($id);

        return $items;
    }

    public function getShoppingListItemsByTaskId(Task $model, Request $request)
    {
        $id = $request->id;
        $items = $this->_shoplService->getShoppingListItemsByTaskId($id);

        return $items;
    }

    // POST
    public function addProduct(ShoppingItem $model, Request $request)
    {
        $shoppingListId = $request->id;
        $data = $request->all();
        $newProduct = $this->_shoplService->addProduct($data, $shoppingListId);

        if ($this->_shoplService->hasErrors())
        {
            return ["errors" => $this->_shoplService->getErrors()];
        }

        return $newProduct;
    }

    // PUT
    public function decreaseQuantity(ShoppingItem $model, Request $request)
    {
        $shoppingListId = $request->id;
        $productId = $request->productId;
        
        $data = $request->all();
        $updatedList = $this->_shoplService->decreaseQuantity($data, $shoppingListId, $productId);

        if ($this->_shoplService->hasErrors())
        {
            return ["errors" => $this->_shoplService->getErrors()];
        }

        return $updatedList;
    }

    public function increaseQuantity(ShoppingItem $model, Request $request)
    {
        $shoppingListId = $request->id;
        $productId = $request->productId;
        
        $data = $request->all();
        $updatedList = $this->_shoplService->increaseQuantity($data, $shoppingListId, $productId);

        if ($this->_shoplService->hasErrors())
        {
            return ["errors" => $this->_shoplService->getErrors()];
        }

        return $updatedList;
    }

    public function removeProduct(ShoppingItem $model, Request $request)
    {
        $shoppingListId = $request->id;
        $productId = $request->productId;
        
        $data = $request->all();
        $updatedList = $this->_shoplService->removeProduct($data, $shoppingListId, $productId);

        if ($this->_shoplService->hasErrors())
        {
            return ["errors" => $this->_shoplService->getErrors()];
        }

        return $updatedList;
    }

    // EVENTS
    // GET
    public function getEvents(Event $model, Request $request)
    {
        $events = $this->_eventService->getEvents();

        return $events;
    }

    public function getEventsForToday(Event $model, Request $request)
    {
        $events = $this->_eventService->getEventsForToday();

        return $events;
    }

    //POST
    public function addEvent(Event $model, Request $request)
    {
        $data = $request->all();
        $event = $this->_eventService->addEvent($data);

        if ($this->_eventService->hasErrors())
        {
            return ["errors" => $this->_eventService->getErrors()];
        }

        return $event;
    }

    // CONTACTS
    // GET
    public function getContacts(Contact $model, Request $request)
    {
        $contacts = $this->_cntctService->getContacts();

        return $contacts;
    }
    // POST
    public function addContact(Contact $model, Request $request)
    {
        $data = $request->all();
        $contact = $this->_cntctService->addContact($data);

        if ($this->_cntctService->hasErrors())
        {
            return ["errors" => $this->_cntctService->getErrors()];
        }

        return $contact;
    }

    public function toggleReachable(Contact $model, Request $request)
    {
        $contactId = $request->id;

        $updatedContact = $this->_cntctService->toggleReachable($contactId);

        if ($this->_cntctService->hasErrors())
        {
            return ["errors" => $this->_cntctService->getErrors()];
        }

        return $updatedContact;
    }
}
