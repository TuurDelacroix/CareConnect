<?php

namespace App\Modules\Services;

use App\Models\Headcarer;
use App\Models\User;

class HeadcarerService extends Service
{
    protected $_rules = [];

    public function __construct(Headcarer $model)
    {
        parent::__construct($model);
    }

    public function getHeadcarer()
    {
        $data = $this->_model->find(1);

        return $data;
    }
}