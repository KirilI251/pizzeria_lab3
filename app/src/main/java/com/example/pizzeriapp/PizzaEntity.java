package com.example.pizzeriapp;

import androidx.room.Entity;     // Ця штука каже, що клас буде табличкою в базі даних
import androidx.room.PrimaryKey;  // А це – що поле буде унікальним ключем, як номер паспорта
import androidx.annotation.NonNull; // Це означає "не можна залишати порожнім" (null)

/**
 * Це наша "Піца". Описує, яку інформацію про кожну піцу ми зберігаємо.
 * У базі даних це буде окрема таблиця під назвою "pizzas".
 */
@Entity(tableName = "pizzas")
public class PizzaEntity {

    /**
     * Номер піци в базі. Кожна піца матиме свій унікальний номер,
     * і база даних сама його призначатиме по порядку.
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * Назва нашої піци, наприклад, "Маргарита" або "Пепероні".
     * Обов'язково має бути вказана.
     */
    @NonNull
    private String name;

    /**
     * Основні інгредієнти піци: "томатний соус, моцарела, шинка, гриби".
     * Теж обов'язково вказуємо.
     */
    @NonNull
    private String ingredients;

    /**
     * Скільки коштує наша піца. Може бути з копійками, тому double.
     */
    private double price;

    /**
     * Розмір піци, зазвичай в сантиметрах (діаметр).
     */
    private int size;

    /**
     * Тут можна написати щось цікаве про піцу: гостра чи ні,
     * на якому тісті, які соуси пасують. Це поле не обов'язкове.
     */
    private String description;

    /**
     * Показує, чи є піца зараз, чи її треба готувати.
     * За замовчуванням, коли додаємо нову, вважаємо, що вона "В наявності".
     */
    @NonNull
    private String status = "В наявності";

    // Далі йдуть методи, щоб дізнатися або змінити інформацію про піцу.
    // Це як кнопки на пульті: одна показує, інша змінює.

    public int getId() {
        return id; // Дізнатися номер піци
    }

    public void setId(int id) {
        this.id = id; // Встановити номер (зазвичай це робить база даних)
    }

    @NonNull
    public String getName() {
        return name; // Дізнатися назву
    }

    public void setName(@NonNull String name) {
        this.name = name; // Змінити назву
    }

    @NonNull
    public String getIngredients() {
        return ingredients; // Дізнатися склад
    }

    public void setIngredients(@NonNull String ingredients) {
        this.ingredients = ingredients; // Змінити склад
    }

    public double getPrice() {
        return price; // Дізнатися ціну
    }

    /**
     * Встановлюємо ціну. Перевіряємо, щоб вона не була меншою за нуль.
     */
    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Ціна повинна бути більшою за нуль");
        }
        this.price = price;
    }

    public int getSize() {
        return size; // Дізнатися розмір
    }

    /**
     * Встановлюємо розмір. Піца повинна мати якийсь розмір.
     */
    public void setSize(int size) {
        if (size > 0) { // Розмір має бути більшим за 0
            this.size = size;
        } else {
            throw new IllegalArgumentException("Розмір повинен бути більшим за нуль!");
        }
    }

    public String getDescription() {
        return description; // Дізнатися опис
    }

    public void setDescription(String description) {
        this.description = description; // Змінити опис
    }

    @NonNull
    public String getStatus() {
        return status; // Дізнатися статус
    }

    /**
     * Встановлюємо статус. Є лише кілька дозволених варіантів для піцерії.
     */
    public void setStatus(@NonNull String status) {
        // Статуси оновлено для відповідності до логіки піцерії
        if (status.equals("В наявності") || status.equals("Готується") || status.equals("Немає в наявності")) {
            this.status = status;
        } else {
            // Якщо вказати якийсь незрозумілий статус, програма повідомить про помилку.
            throw new IllegalArgumentException("Такого статусу немає! Можна: 'В наявності', 'Готується', 'Немає в наявності'.");
        }
    }
}