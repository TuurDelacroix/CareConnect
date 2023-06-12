<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PatientContact extends Model
{
    use HasFactory;

    protected $table = 'patient_contact';

    protected $fillable = [
        'patient_id',
        'contact_id',
    ];

    public function patient()
    {
        return $this->belongsTo(Patient::class);
    }

    public function contact()
    {
        return $this->belongsTo(Contact::class);
    }
}
