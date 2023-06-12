<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('tasks', function (Blueprint $table) {
            $table->id();
            $table->string('title');
            $table->enum('type', ['DO_SHOPPING', 'ACCOMPANY']);
            $table->enum('status', ['REQUESTED', 'IN_PROGRESS', 'COMPLETED']);
            $table->timestamp('date');
            $table->unsignedBigInteger('shopping_list_id')->nullable();
            $table->timestamps();

            $table->foreign('shopping_list_id')
                    ->references('id')
                    ->on('shopping_lists')
                    ->onDelete('cascade');
        });
        
        Schema::create('shopping_items', function (Blueprint $table) {
            $table->id();
            $table->string('name');
            $table->string('description')->nullable();
            $table->string('image')->nullable();
            $table->integer('quantity')->default(0);
            $table->unsignedBigInteger('shopping_list_id')->nullable();
            $table->timestamps();

            $table->foreign('shopping_list_id')
                    ->references('id')
                    ->on('shopping_lists')
                    ->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('tasks');
        Schema::dropIfExists('shopping_items');
        Schema::dropIfExists('shopping_item_task');
    }
};
