package com.example.pizzeriapp;

import androidx.lifecycle.LiveData; // Потрібно для LiveData, щоб дані оновлювалися автоматично в UI
import androidx.room.Dao;          // Говорить Room, що це Data Access Object (об'єкт для доступу до даних)
import androidx.room.Delete;       // Анотація для методу видалення запису
import androidx.room.Insert;       // Анотація для методу вставки нового запису
import androidx.room.Query;        // Анотація для написання власних SQL-запитів
import androidx.room.Update;       // Анотація для методу оновлення запису

import java.util.List;

/**
 * Інтерфейс для роботи з піцами в базі даних.
 * Room сам створить необхідний код для реалізації цих методів.
 * Ми просто описуємо, які дії хочемо виконувати.
 */
@Dao
public interface PizzaDao {

    /**
     * Дістає всі-всі піци з таблиці "pizzas".
     * Вони будуть відсортовані за номером (id) так, щоб новіші були першими.
     * LiveData дозволяє екрану автоматично оновлювати список, якщо щось змінилося в базі.
     */
    @Query("SELECT * FROM pizzas ORDER BY id DESC")
    LiveData<List<PizzaEntity>> getAllPizzas();

    /**
     * Додає нову піцу в базу.
     * @param pizza - це та піца, яку ми хочемо зберегти.
     */
    @Insert
    void insertPizza(PizzaEntity pizza);

    /**
     * Оновлює інформацію про вже існуючу піцу.
     * @param pizza - піца з новими даними, яка замінить стару в базі.
     */
    @Update
    void updatePizza(PizzaEntity pizza);

    /**
     * Видаляє піцу з бази.
     * @param pizza - та піца, яку треба видалити.
     */
    @Delete
    void deletePizza(PizzaEntity pizza);

    /**
     * Знаходить і повертає одну конкретну піцу за її унікальним номером (id).
     * @param pizzaId - номер піци, яку шукаємо.
     * @return Знайдена піца або null, якщо такої немає.
     */
    // Назва таблиці "pizzas" і поле "id"
    @Query("SELECT * FROM pizzas WHERE id = :pizzaId LIMIT 1")
    PizzaEntity getPizzaById(int pizzaId);
}