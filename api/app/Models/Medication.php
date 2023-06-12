<?php

namespace App\Models;

use Carbon\Carbon;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Medication extends Model
{
    use HasFactory;

    protected $fillable = [
        'type',
        'name',
        'dose',
        'is_taken',
        'schedule_date',
        'schedule_time',
    ];

    public function patient()
    {
        return $this->belongsTo(Patient::class);
    }
}
