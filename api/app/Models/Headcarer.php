<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Headcarer extends Model
{
    use HasFactory;

    
    protected $fillable = [
        'first_name',
        'last_name',
        'profile_pic',
        'phone_number',
    ];
}
