package com.example.pizzeriapp;

import android.os.Bundle; // Для збереження стану Activity
import androidx.appcompat.app.AppCompatActivity; // Базовий клас для Activity з підтримкою App Bar
import androidx.lifecycle.ViewModelProvider; // Допомагає отримати ViewModel
import androidx.recyclerview.widget.LinearLayoutManager; // Розташовує елементи списку один за одним
import androidx.recyclerview.widget.RecyclerView; // Сам список для відображення даних
import com.google.android.material.button.MaterialButton; // Імпорт для кнопки
import android.widget.Toast; // Для показу повідомлень користувачу

import java.util.ArrayList; // Для створення порожнього списку на початку

/**
 * Головний екран нашого додатку "Піцерія".
 * Тут відображається список піц, є кнопка для додавання нової,
 * а також можна редагувати та видаляти існуючі піци.
 */
public class MainActivity extends AppCompatActivity {

    // Наша ViewModel, яка керує даними про піци.
    private PizzaViewModel pizzaViewModel;
    // Наш Адаптер, який "знає", як відобразити кожну піцу у списку.
    private PizzaAdapter pizzaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Встановлюємо XML-макет для цього екрану (activity_main.xml).
        setContentView(R.layout.activity_main);

        // Знаходимо наш RecyclerView в макеті
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        // Кажемо йому, як розташовувати елементи (вертикально, один за одним)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true); // Оптимізація: якщо розмір елементів не змінюється

        // Створюємо наш адаптер для піц, поки що з порожнім списком
        pizzaAdapter = new PizzaAdapter(new ArrayList<>());
        // "Прив'язуємо" адаптер до RecyclerView
        recyclerView.setAdapter(pizzaAdapter);

        // Отримуємо екземпляр нашої PizzaViewModel.
        // ВАЖЛИВО: Вам потрібно буде створити клас PizzaViewModel за аналогією до DessertViewModel
        pizzaViewModel = new ViewModelProvider(this).get(PizzaViewModel.class);

        // Тепер "підписуємося" на зміни у списку всіх піц, який є у ViewModel.
        // Коли дані в базі зміняться, ViewModel оновить LiveData, а цей код автоматично викличеться.
        pizzaViewModel.getAllPizzas().observe(this, pizzaEntities -> {
            // Отримали новий список pizzaEntities, передаємо його в адаптер.
            pizzaAdapter.setPizzaList(pizzaEntities);
        });

        // Знаходимо нашу кнопку "Додати піцу" (MaterialButton)
        // ВАЖЛИВО: Переконайтесь, що ID кнопки у вашому activity_main.xml - R.id.buttonAddPizza
        MaterialButton buttonAddPizza = findViewById(R.id.buttonAddPizza);
        // Встановлюємо обробник натискання на цю кнопку
        buttonAddPizza.setOnClickListener(view -> {
            // Коли кнопку натиснуто, показуємо наш діалог додавання піци
            AddPizzaDialog.show(this, newPizza -> {
                // Цей код (callback) виконається, коли користувач введе дані в діалозі і натисне "Додати".
                pizzaViewModel.insert(newPizza); // Кажемо ViewModel додати цю піцу в базу
            }, null); // Передаємо null, бо це створення нової піци, а не редагування існуючої.
        });

        // Встановлюємо обробник короткого кліку на елемент списку (для редагування)
        pizzaAdapter.setOnPizzaClickListener(pizza -> {
            // pizza - це та піца, на яку клікнули.
            // Зберігаємо її ID, бо сам об'єкт pizza може змінитися, поки діалог відкритий.
            final int pizzaId = pizza.getId();

            // Показуємо діалог, передаючи в нього поточну піцу для редагування.
            AddPizzaDialog.show(this, updatedPizza -> {
                // Цей код (callback) виконається, коли користувач змінить дані в діалозі і натисне "Оновити".
                updatedPizza.setId(pizzaId); // Важливо! Встановлюємо ID для оновлення правильного запису в базі.
                pizzaViewModel.update(updatedPizza); // Кажемо ViewModel оновити цю піцу.
            }, pizza); // Передаємо об'єкт піци, який хочемо редагувати.
        });

        // Встановлюємо обробник довгого кліку на елемент списку (для видалення)
        pizzaAdapter.setOnPizzaLongClickListener(pizza -> {
            // pizza - піца, на якій зробили довгий клік.
            // Показуємо стандартний діалог підтвердження перед видаленням.
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Видалити піцу?") // Заголовок діалогу
                    // Повідомлення, що показує назву піци, яку збираємося видалити
                    .setMessage("Ви впевнені, що хочете видалити '" + pizza.getName() + "'?")
                    .setPositiveButton("Видалити", (dialog, which) -> {
                        // Якщо користувач натиснув "Видалити"
                        pizzaViewModel.delete(pizza); // Кажемо ViewModel видалити цю піцу
                        // Повідомлення про успішне видалення
                        Toast.makeText(MainActivity.this, "'" + pizza.getName() + "' видалено успішно!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Скасувати", null) // Кнопка "Скасувати" просто закриває діалог
                    .show(); // Показуємо діалог
        });
    }
}