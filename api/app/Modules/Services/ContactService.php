<?php

namespace App\Modules\Services;

use App\Models\Contact;
use App\Models\Patient;
use Carbon\Carbon;
use Illuminate\Support\Facades\Validator;

class ContactService extends Service
{
    protected $add_rules = [
        'first_name' => 'required|string',
        'last_name' => 'required|string',
        'profile_pic' => 'nullable|string',
        'phone_number' => 'required|string',
        'reachable' => 'required|boolean',
    ];

    public function __construct(Contact $model)
    {
        parent::__construct($model);
    }

    public function getContacts()
    {
        $contacts = Contact::all();
        
        return $contacts;
    }

    public function addContact($data)
    {
        $validator = Validator::make($data, $this->add_rules);

        if ($validator->fails()) {
            return response()->json(['errors' => $validator->errors()], 400);
        }
        
        $contact = new Contact([
            'first_name' => $data['first_name'],
            'last_name' => $data['last_name'],
            'profile_pic' => $data['profile_pic'],
            'phone_number' => $data['phone_number'],
            'reachable' => $data['reachable'],
        ]);

        $patient = Patient::findOrFail(1);
        $patient->contacts()->attach($contact->id);

        $contact->save();

        return $contact;
    }

    public function toggleReachable($contactId)
    {
        $contact = Contact::findOrFail($contactId);
        $contact->reachable = !$contact->reachable;
        $contact->save();

        return $contact;
    }
}