<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use ReflectionClass;

/* 
 * @method static PIL()
 * @method static BRUISTABLET()
 * @method static KAUWTABLET()
 */

class MedicationType extends Model
{
    const PIL = 'PIL';
    const BRUISTABLET = 'BRUISTABLET';
    const KAUWTABLET = 'KAUWTABLET';

    protected $fillable = [
        'type',
    ];

    public function medications()
    {
        return $this->belongsToMany(Medication::class);
    }

    public static function getValues()
    {
        $reflectionClass = new ReflectionClass(static::class);
        return $reflectionClass->getConstants();
    }
}
