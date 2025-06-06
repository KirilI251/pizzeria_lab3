package com.example.pizzeriapp; // Переконайтесь, що назва пакету правильна

import android.view.LayoutInflater; // Допомагає "надути" (створити) вигляд з XML-файлу
import android.view.View;          // Базовий клас для всіх елементів UI
import android.view.ViewGroup;     // Контейнер для інших View
import android.widget.TextView;    // Для відображення тексту

import androidx.annotation.NonNull; // Позначка, що щось не може бути null
import androidx.recyclerview.widget.RecyclerView; // Для створення списків

import java.util.List;       // Для роботи зі списками
import java.util.Locale;     // Для форматування тексту, наприклад, ціни відповідно до мови

/**
 * Це Адаптер. Він як міст між нашими даними (списком піц)
 * та тим, як вони відображаються на екрані у вигляді списку (RecyclerView).
 * Він відповідає за створення кожного елемента списку та заповнення його даними.
 */
public class PizzaAdapter extends RecyclerView.Adapter<PizzaAdapter.PizzaViewHolder> {

    // Тут зберігатиметься наш список піц, який ми хочемо показати
    private List<PizzaEntity> pizzaList;

    // Це "слухачі" натискань на елементи списку.
    // MainActivity підпишеться на них, щоб знати, коли користувач щось натиснув.

    // Для звичайного (короткого) кліку
    public interface OnPizzaClickListener {
        void onPizzaClick(PizzaEntity pizza); // Передаємо натиснуту піцу
    }
    private OnPizzaClickListener clickListener;

    // Для довгого кліку (зазвичай використовується для видалення)
    public interface OnPizzaLongClickListener {
        void onPizzaLongClick(PizzaEntity pizza); // Передаємо натиснуту піцу
    }
    private OnPizzaLongClickListener longClickListener;

    /**
     * Конструктор Адаптера. Сюди ми передаємо початковий список піц.
     * @param pizzaList Список піц для відображення.
     */
    public PizzaAdapter(List<PizzaEntity> pizzaList) {
        this.pizzaList = pizzaList;
    }

    /**
     * Цей метод викликається, коли RecyclerView потрібно створити новий "контейнер"
     * (ViewHolder) для одного елемента списку.
     * Ми тут вказуємо, який XML-макет використовувати для одного рядка.
     */
    @NonNull
    @Override
    public PizzaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Беремо наш XML-файл item_pizza.xml і створюємо з нього View-об'єкт
        // ВАЖЛИВО: Переконайтесь, що ви перейменували свій layout файл на R.layout.item_pizza
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pizza, parent, false);
        return new PizzaViewHolder(itemView); // Повертаємо наш новий PizzaViewHolder
    }

    /**
     * Цей метод заповнює вже створений ViewHolder даними з конкретної піци.
     * @param holder "Контейнер" (PizzaViewHolder), який треба заповнити.
     * @param position Номер (індекс) піци в нашому списку pizzaList.
     */
    @Override
    public void onBindViewHolder(@NonNull PizzaViewHolder holder, int position) {
        // Беремо конкретну піцу зі списку за її номером
        PizzaEntity currentPizza = pizzaList.get(position);

        // Тепер заповнюємо текстові поля в нашому ViewHolder'і даними з піци
        holder.pizzaNameText.setText(currentPizza.getName()); // Встановлюємо назву

        // Формуємо рядок з інгредієнтами та ціною для другого текстового поля
        // Припускаємо, що у PizzaEntity є метод getIngredients()
        String info = String.format(Locale.getDefault(), "%s - %.2f грн",
                currentPizza.getIngredients(), currentPizza.getPrice());
        holder.pizzaInfoText.setText(info); // Встановлюємо інформацію

        // Налаштовуємо реакцію на короткий клік по цьому елементу списку
        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onPizzaClick(currentPizza); // Повідомляємо "слухача" про клік
            }
        });

        // Налаштовуємо реакцію на довгий клік
        holder.itemView.setOnLongClickListener(view -> {
            if (longClickListener != null) {
                longClickListener.onPizzaLongClick(currentPizza); // Повідомляємо "слухача"
                return true; // Кажемо системі, що ми обробили цей довгий клік
            }
            return false; // Якщо слухача немає, кажемо, що не обробили
        });
    }

    /**
     * Повертає кількість піц у нашому списку.
     * RecyclerView використовує це, щоб знати, скільки елементів малювати.
     */
    @Override
    public int getItemCount() {
        // Якщо список не порожній, повертаємо його розмір, інакше 0
        return pizzaList != null ? pizzaList.size() : 0;
    }

    /**
     * Цей метод дозволяє оновити список піц в адаптері.
     * Наприклад, коли ми додали нову піцу або видалили стару.
     * @param newPizzaList Новий список піц.
     */
    public void setPizzaList(List<PizzaEntity> newPizzaList) {
        this.pizzaList = newPizzaList;
        notifyDataSetChanged(); // Дуже важлива команда! Повідомляє RecyclerView, що дані змінилися і треба все перемалювати.
    }

    /**
     * ViewHolder – це такий собі "тримач" для елементів вигляду (View) одного рядка списку.
     * Замість того, щоб кожного разу шукати TextView за ID, ми робимо це один раз
     * і зберігаємо посилання тут. Це робить прокрутку списку швидшою.
     */
    public static class PizzaViewHolder extends RecyclerView.ViewHolder {
        TextView pizzaNameText;  // Тут буде назва піци
        TextView pizzaInfoText;  // Тут буде склад і ціна

        public PizzaViewHolder(@NonNull View itemView) {
            super(itemView); // Викликаємо конструктор батька
            // Знаходимо наші текстові поля в макеті item_pizza.xml за їх ID
            pizzaNameText = itemView.findViewById(R.id.textPizzaName);
            pizzaInfoText = itemView.findViewById(R.id.textPizzaInfo);
        }
    }

    // Методи, щоб MainActivity могла "підписатися" на події кліків

    public void setOnPizzaClickListener(OnPizzaClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnPizzaLongClickListener(OnPizzaLongClickListener listener) {
        this.longClickListener = listener;
    }
}