<?php

namespace App\Modules\Services;

use App\Models\Medication;
use App\Models\MedicationType;
use App\Models\Patient;
use Carbon\Carbon;
use Illuminate\Support\Facades\Validator;

class MedicationService extends Service
{
    protected $add_rules = [
        'type' => 'required|string',
        'type.*' =>  "in:" . MedicationType::PIL . "," . MedicationType::BRUISTABLET . "," . MedicationType::KAUWTABLET,
        'name' => 'required|string|max:255',
        'dose' => 'required|string|max:255',
        'is_taken' => 'required|boolean',
        'schedule_date' => 'required|date',
        'schedule_time' => 'required|date_format:H:i',
    ];

    public function __construct(Medication $model)
    {
        parent::__construct($model);
    }

    public function getMedication()
    {
        $medications = Medication::all();

        foreach ($medications as $medication) {
            $medication->schedule_date = Carbon::parse($medication->schedule_date)->toDateString();
            $medication->schedule_time = Carbon::parse($medication->schedule_time)->toTimeString();
        }

        return response()->json($medications);
    }

    public function getMedicationForToday()
    {
        $medications = Medication::whereDate('schedule_date', '<=', now())
            ->whereDate('schedule_date', '>=', now())
            ->get();

        foreach ($medications as $medication) {
            $medication->schedule_date = Carbon::parse($medication->schedule_date)->toDateString();
            $medication->schedule_time = Carbon::parse($medication->schedule_time)->toTimeString();
        }

        return response()->json($medications);
    }

    public function getMedicationForFuture()
    {
        $medications = Medication::whereDate('schedule_date', '>', now())
            ->get();

        foreach ($medications as $medication) {
            $medication->schedule_date = Carbon::parse($medication->schedule_date)->toDateString();
            $medication->schedule_time = Carbon::parse($medication->schedule_time)->toTimeString();
        }
        return response()->json($medications);
    }

    public function addMedication($data)
    {

        $validator = Validator::make($data, $this->add_rules);

        if ($validator->fails()) {
            return response()->json(['errors' => $validator->errors()], 400);
        }

        $medication = new Medication();
        $medication->type = $data['type'];
        $medication->name = $data['name'];
        $medication->dose = $data['dose'];
        $medication->is_taken = $data['is_taken'];
        //$medication->schedule_date = $data['schedule_date'];
        $medication->schedule_date = Carbon::parse($data['schedule_date'])->toDate();
        //$medication->schedule_time = $data['schedule_time'];
        $medication->schedule_time = Carbon::parse($data['schedule_time'])->toDate();
        $medication->save();

        // For simulation only (to set it autmatically to Annie)
        $patient = Patient::findOrFail(1);
        $patient->medications()->attach($medication->id);

        return response()->json(['message' => 'Medication added successfully.'], 201);
    }

    public function markMedicationAsTaken($id)
    {
        $medication = Medication::findOrFail($id);
        $medication->is_taken = true;
        $medication->save();
    
        return response()->json(Medication::all());
    }

    public function markAllMedicationForTodayAsTaken()
    {
        $medications = Medication::whereDate('schedule_date', Carbon::today())
                                ->update(['is_taken' => true]);

        return response()->json(Medication::all());
    }
}
