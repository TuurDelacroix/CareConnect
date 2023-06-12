<?php

namespace App\Modules\Services;

use App\Models\Event;
use App\Models\Patient;
use Carbon\Carbon;
use Illuminate\Support\Facades\Validator;

class EventService extends Service
{
    protected $add_rules = [
        'title' => 'required|string',
        'date' => 'required|date'
    ];

    public function __construct(Event $model)
    {
        parent::__construct($model);
    }

    public function getEvents()
    {
        $events = Event::orderBy('date', 'asc')->get();

        foreach ($events as $event)
        {
            $event->date = Carbon::parse($event->date)->toDateTimeLocalString();
        }

        return response()->json($events);
    }

    public function getEventsForToday()
    {
        $events = Event::where('date', '>=', Carbon::today()->startOfDay())
                    ->where('date', '<', Carbon::tomorrow()->startOfDay())
                    ->get();
        
        foreach ($events as $event)
        {
            $event->date = Carbon::parse($event->date)->toDateTimeLocalString();
        }
                
        return response()->json($events);
    }

    public function addEvent($data)
    {
        $validator = Validator::make($data, $this->add_rules);

        if ($validator->fails()) {
            return response()->json(['errors' => $validator->errors()], 400);
        }
        
        $event = new Event([
            'title' => $data['title'],
            'date' => $data['date']
        ]);

        $patient = Patient::findOrFail(1);

        $event->patient()->associate($patient);
        $event->save();

        return $event;
        //$patient->events()->attach($event->id);
    }
}