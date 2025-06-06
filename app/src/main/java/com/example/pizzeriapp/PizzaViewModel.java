package com.example.pizzeriapp;

import android.app.Application; // Потрібен для ViewModel, щоб мати доступ до ресурсів, якщо знадобиться

import androidx.annotation.NonNull; // Помітка, що параметр або змінна не можуть бути null
import androidx.lifecycle.AndroidViewModel; // Базовий клас для ViewModel, яка "знає" про контекст додатка
import androidx.lifecycle.LiveData;     // Клас для даних, за якими можна "спостерігати" (наприклад, з UI)

import java.util.List; // Для використання списків
import java.util.concurrent.ExecutorService; // Штука для виконання задач в окремому потоці
import java.util.concurrent.Executors;   // Допомагає створювати ExecutorService

/**
 * Ця ViewModel керує даними про піци для нашого UI (екранів).
 * Вона бере дані з бази та готує їх для показу.
 * Також вона обробляє дії користувача, пов'язані з даними (додати, оновити, видалити).
 */
public class PizzaViewModel extends AndroidViewModel {

    // Наш інструмент (DAO) для роботи з таблицею піц у базі даних.
    // final означає, що після присвоєння значення цю змінну не можна буде змінити.
    private final com.example.pizzeriapp.PizzaDao pizzaDao;

    // Список усіх піц, який "живий". Тобто, якщо дані в базі зміняться,
    // цей список автоматично оновить те, що бачить користувач на екрані.
    private final LiveData<List<PizzaEntity>> allPizzas;

    // Створюємо окремий потік для роботи з базою даних.
    // Це важливо, щоб не "гальмувати" основний потік, де малюється інтерфейс.
    // newSingleThreadExecutor() означає, що всі операції з базою будуть виконуватися по черзі в одному потоці.
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Це конструктор. Він викликається, коли система створює нашу ViewModel.
     * @param application Посилання на наш додаток, щоб отримати доступ до бази даних.
     */
    public PizzaViewModel(@NonNull Application application) {
        super(application); // Викликаємо конструктор батьківського класу
        // Отримуємо "зв'язок" з нашою базою даних
        AppDatabase db = AppDatabase.getInstance(application);
        // Через базу даних отримуємо наш інструмент (DAO) для роботи з піцами
        pizzaDao = db.pizzaDao();
        // Запитуємо у DAO список усіх піц. Цей список буде "живим".
        allPizzas = pizzaDao.getAllPizzas();
    }

    /**
     * Цей метод дозволяє іншим частинам програми (наприклад, нашому Activity)
     * отримати "живий" список усіх піц.
     * @return LiveData зі списком PizzaEntity.
     */
    public LiveData<List<PizzaEntity>> getAllPizzas() {
        return allPizzas;
    }

    /**
     * Додає нову піцу до бази даних.
     * Робить це в окремому потоці, щоб не заблокувати UI.
     * @param pizza - об'єкт піци, який потрібно зберегти.
     */
    public void insert(PizzaEntity pizza) {
        executorService.execute(() -> pizzaDao.insertPizza(pizza));
    }

    /**
     * Оновлює інформацію про існуючу піцу в базі даних.
     * Робить це в окремому потоці.
     * @param pizza - об'єкт піци з новими даними.
     */
    public void update(PizzaEntity pizza) {
        executorService.execute(() -> pizzaDao.updatePizza(pizza));
    }

    /**
     * Видаляє піцу з бази даних.
     * Робить це в окремому потоці.
     * @param pizza - об'єкт піци, який потрібно видалити.
     */
    public void delete(PizzaEntity pizza) {
        executorService.execute(() -> pizzaDao.deletePizza(pizza));
    }

    /**
     * Цей метод викликається, коли ViewModel більше не потрібна і буде знищена.
     * Важливо "закрити" наш ExecutorService, щоб уникнути витоків ресурсів.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown(); // "Вимикаємо" наш окремий потік
    }
}