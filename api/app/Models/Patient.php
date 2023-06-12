<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Patient extends Model
{
    use HasFactory;

    protected $fillable = [
        'first_name',
        'last_name',
        'profile_pic',
        'phone_number',
        'head_carer_id',
    ];

    public function Headcarer()
    {
        return $this->belongsTo(Headcarer::class, 'head_carer_id');
    }

    public function contacts()
    {
        //return $this->hasMany(Contact::class);
        // Use the "contacts" table as the pivot table
        // Use the "contact_id" column on the pivot table to reference the related Contact model
        // Use the "patient_id" column on the pivot table to reference the current Patient model
        return $this->belongsToMany(Contact::class, 'patient_contact', 'patient_id', 'contact_id');
    }

    public function medications()
    {
        return $this->belongsToMany(Medication::class, 'patient_medication', 'patient_id', 'medication_id');
    }

    public function tasks()
    {
        return $this->belongsToMany(Task::class, 'patient_task', 'patient_id', 'task_id')->withTimestamps();
    }

    public function tasksForToday()
    {
        return $this->hasMany(Task::class)->where('date', today());
    }

    public function tasksForFuture()
    {
        return $this->hasMany(Task::class)->where('date', '>', today());
    }

    public function events()
    {
        return $this->hasMany(Event::class);
    }
}
