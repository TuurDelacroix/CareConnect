<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Contact extends Model
{
    use HasFactory;

    protected $fillable = [
        'first_name',
        'last_name',
        'profile_pic',
        'phone_number',
        'reachable',
    ];

    protected $casts = [
        'profile_pic' => 'integer',
        'reachable' => 'boolean',
    ];

    public function patients()
    {
        //return $this->belongsTo(Patient::class);
        return $this->belongsToMany(Patient::class, 'patient_contact', 'contact_id', 'patient_id');
    }
}
