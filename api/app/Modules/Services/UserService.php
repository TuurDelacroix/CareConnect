<?php

namespace App\Modules\Services;

use App\Models\Patient;

class UserService extends Service
{
    protected $_rules = [];

    public function __construct(Patient $model)
    {
        parent::__construct($model);
    }

    public function getCurrentAppUser()
    {
        $data = $this->_model->find(1);
        return $data;
    }

    public function getCurrentAppUserFull()
    {
        $data = $this->_model->with('headcarer','contacts', 'medications', 'events', 'tasks.shoppingList.shoppingItems')->find(1);
        return $data;
    }
}