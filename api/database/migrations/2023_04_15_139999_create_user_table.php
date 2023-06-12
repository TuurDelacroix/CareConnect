<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up()
    {
        
        Schema::create('headcarers', function (Blueprint $table) {
            $table->id();
            $table->string('first_name');
            $table->string('last_name');
            $table->string('profile_pic')->nullable();
            $table->string('phone_number');
            $table->timestamps();
        });
        
        Schema::create('patients', function (Blueprint $table) {
            $table->id();
            $table->string('first_name');
            $table->string('last_name');
            $table->string('profile_pic')->nullable();
            $table->string('phone_number');
            $table->unsignedBigInteger('head_carer_id');
            $table->timestamps();

            $table->foreign('head_carer_id')
                  ->references('id')
                  ->on('headcarers')
                  ->onDelete('cascade');
        });

    }

    public function down()
    {
        Schema::dropIfExists('patients');
        Schema::dropIfExists('headcarers');
    }
};
