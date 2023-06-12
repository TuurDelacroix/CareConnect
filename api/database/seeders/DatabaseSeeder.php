<?php

namespace Database\Seeders;

// use Illuminate\Database\Console\Seeds\WithoutModelEvents;

use App\Models\Contact;
use App\Models\Event;
use App\Models\HeadCarer;
use App\Models\Medication;
use App\Models\MedicationType;
use App\Models\Patient;
use App\Models\PatientContact;
use App\Models\Schedule;
use App\Models\ShoppingItem;
use App\Models\ShoppingList;
use App\Models\Task;
use App\Models\TaskStatus;
use App\Models\TaskType;
use App\Models\User;
use Carbon\Carbon;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        // PATIENT AND THEIR HEADCARER
        $headCarer = Headcarer::create([
            'first_name' => 'Tuur',
            'last_name' => 'Delacroix',
            'profile_pic' => 'person_image.png',
            'phone_number' => '+320471448187'
        ]);

        $patient = Patient::create([
            'first_name' => 'Annie',
            'last_name' => 'Biljet',
            'profile_pic' => 'person_image.png',
            'phone_number' => '+320473607375',
            'head_carer_id' => $headCarer->id
        ]);

        // CONTACTS
        $contacts = collect([
            ['first_name' => 'Danny', 'last_name' => 'Delacroix', 'phone_number' => '+320479289525', 'reachable' => true],
            ['first_name' => 'Brian', 'last_name' => 'Bota', 'phone_number' => '+320473288349', 'reachable' => false]
        ]);

        $contacts->each(function ($contact) use ($patient) {
            Contact::create([
                'first_name' => $contact['first_name'],
                'last_name' => $contact['last_name'],
                'phone_number' => $contact['phone_number'],
                'reachable' => $contact['reachable']
            ]);
        });

        $patients = Patient::all();
        $contacts = Contact::all();

        foreach ($patients as $patient) {

            // Create PatientContact models for each selected contact
            foreach ($contacts as $contact) {
                $patientContact = new PatientContact([
                    'patient_id' => $patient->id,
                    'contact_id' => $contact->id,
                ]);
                $patientContact->save();
            }
        }

        // MEDICATION
        $medications = [
            [
                'type' => MedicationType::PIL,
                'name' => 'LUCOVITAAL: Mentale Focus',
                'dose' => '1 tablet',
                'is_taken' => false,
                'schedule_date' => Carbon::create(2023, 6, 12),
                'schedule_time' => Carbon::create(0, 0, 0, 8, 33, 0)
            ],
            [
                'type' => MedicationType::BRUISTABLET,
                'name' => 'Dafalgan Forte',
                'dose' => '500 mg',
                'is_taken' => false,
                'schedule_date' => Carbon::create(2023, 6, 12),
                'schedule_time' => Carbon::create(0, 0, 0, 19, 10, 0)
            ],
            [
                'type' => MedicationType::KAUWTABLET,
                'name' => 'Supradyn: Energie Gummies',
                'dose' => '1 capsule',
                'is_taken' => false,
                'schedule_date' => Carbon::create(2023, 6, 12),
                'schedule_time' => Carbon::create(0, 0, 0, 12, 0, 0)
            ],
        ];

        foreach ($medications as $medication) {
            Medication::create([
                'type' => $medication['type'],
                'name' => $medication['name'],
                'dose' => $medication['dose'],
                'is_taken' => $medication['is_taken'],
                'schedule_date' => $medication['schedule_date'],
                'schedule_time' => $medication['schedule_time'],
            ]);
        }

        foreach ($patients as $patient) {
            $medications = Medication::all();
            foreach ($medications as $medication) {
                $patient->medications()->attach($medication);
            }
        }

        
        // EVENTS
        $events = collect([
            [
                'title' => 'Biljarten met Tuur',
                'date' => '2023-06-12 15:00:00'
            ],
            [
                'title' => 'Michelle komt langs',
                'date' => '2023-06-12 18:30:00'
            ]
        ]);

        $events->each(function ($event) use ($patient) {
            Event::create([
                'title' => $event['title'],
                'date' => $event['date'],
                'patient_id' => $patient->id
            ]);
        });


        // TASKS & SHOPPINGLIST

        $tasks = [
            [
                "title" => "Mediamarkt",
                "type" => TaskType::DO_SHOPPING,
                "status" => TaskStatus::REQUESTED,
                "date" => "2023-06-12 23:00:00",
                "shopping_list_items" => null
            ],
            [
                "title" => "Dreamland",
                "type" => TaskType::DO_SHOPPING,
                "status" => TaskStatus::IN_PROGRESS,
                "date" => "2023-06-12 10:00:00",
                "shopping_list_items" => [
                    ["name" => "Pluche Beer", "description" => "Bruine kleur", "image" => "https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcRFFXLqUAMSL-iHt1ZXMX_adS3K4ydvPNqdPvYqS-0T8szc6oPGQzU9yZ7ASR0ipgKWKjB48eZMPVfSlCvUnssZX0yO6mmyNhETP3xv-ElF5C3grN1FQkxP&usqp=CAE", "quantity" => 1]
                ]
            ],
            [
                "title" => "Colruyt",
                "type" => TaskType::DO_SHOPPING,
                "status" => TaskStatus::REQUESTED,
                "date" => "2023-06-13 10:00:00",
                "shopping_list_items" => [
                    ["name" => "AAA Batterijen", "description" => "Duracell", "image" => "https://cdn.webshopapp.com/shops/39288/files/418116280/duracell-oplaadbare-aaa-batterijen-750mah-4-stuks.jpg", "quantity" => 1],
                    ["name" => "AA Batterijen", "description" => "Duracell", "image" => "https://www.duracell.be/upload/sites/11/2020/07/1016826_alkaline_mainline-plus_AA_4_primary1.png", "quantity" => 2],
                    ["name" => "Sinaasappelen", "description" => "Oranje", "image" => "https://static.colruytgroup.com/images/500x500/std.lang.all/63/94/asset-1166394.jpg", "quantity" => 4],
                ]
            ],
            [
                "title" => "Biljarten met Tuur",
                "type" => TaskType::ACCOMPANY,
                "status" => TaskStatus::COMPLETED,
                "date" => "2023-06-12 18:00:00",
                "shopping_list_items" => null
            ],
            [
                "title" => "Koffie met Tuur",
                "type" => TaskType::ACCOMPANY,
                "status" => TaskStatus::REQUESTED,
                "date" => "2023-06-14 15:00:00",
                "shopping_list_items" => null
            ]
        ];

        foreach ($tasks as $taskData) {
            $task = new Task();
            $task->title = $taskData['title'];
            $task->type = $taskData['type'];
            $task->status = $taskData['status'];
            $task->date = Carbon::create($taskData['date']);
            $task->save();

            if ($taskData['shopping_list_items']) {
                $shoppingList = new ShoppingList();
                $shoppingList->name = $task->title;
                $shoppingList->save();

                foreach ($taskData['shopping_list_items'] as $itemData) {
                    $item = new ShoppingItem();
                    $item->name = $itemData['name'];
                    $item->description = $itemData['description'];
                    $item->image = $itemData['image'];
                    $item->quantity = $itemData['quantity'];
                    $item->shopping_list_id = $shoppingList->id;
                    $item->save();
                }

                $task->shopping_list_id = $shoppingList->id;
                $task->save();
            }

            $patient->tasks()->attach($task);
        }

    }
}
