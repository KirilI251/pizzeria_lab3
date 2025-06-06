package com.example.pizzeriapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast; // Для показу повідомлень користувачу (наприклад, про помилки)

import com.google.android.material.textfield.TextInputEditText;

/**
 * Цей клас відповідає за показ діалогового вікна,
 * де користувач може додати нову піцу або відредагувати існуючу.
 */
public class AddPizzaDialog {

    /**
     * Інтерфейс (як контракт), щоб повідомити MainActivity (або хто викликав діалог),
     * коли піцу успішно додано або оновлено.
     */
    public interface AddPizzaCallback {
        void onPizzaAdded(PizzaEntity pizza); // Метод тепер приймає PizzaEntity
    }

    /**
     * Головний метод, який створює і показує діалог.
     * @param context Контекст, звідки викликається діалог (зазвичай це MainActivity).
     * @param callback Об'єкт, який реалізує AddPizzaCallback, для отримання результату.
     * @param pizzaToEdit Піца, яку ми редагуємо. Якщо це null, значить ми додаємо нову піцу.
     */
    public static void show(Context context, AddPizzaCallback callback, PizzaEntity pizzaToEdit) {
        // Беремо наш XML-макет dialog_add_pizza.xml і "надуваємо" його (створюємо View)
        // ВАЖЛИВО: Переконайтесь, що ви перейменували свій layout файл на R.layout.dialog_add_pizza
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_pizza, null);

        // Знаходимо всі наші поля вводу та Spinner в макеті dialog_add_pizza.xml
        TextInputEditText editName = dialogView.findViewById(R.id.editPizzaName);
        TextInputEditText editIngredients = dialogView.findViewById(R.id.editPizzaIngredients);
        TextInputEditText editPrice = dialogView.findViewById(R.id.editPizzaPrice);
        TextInputEditText editSize = dialogView.findViewById(R.id.editPizzaSize); // Раніше було 'Weight'
        TextInputEditText editDescription = dialogView.findViewById(R.id.editPizzaDescription);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);

        // Готуємо список статусів для нашого випадаючого списку (Spinner)
        String[] statuses = {"В наявності", "Готується", "Немає в наявності"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Визначаємо, чи це режим редагування чи додавання
        boolean isEditing = (pizzaToEdit != null);
        String dialogTitle = isEditing ? "Редагувати піцу" : "Додати нову піцу";
        String positiveButtonText = isEditing ? "Оновити" : "Додати";

        // Якщо ми редагуємо існуючу піцу, заповнюємо поля
        if (isEditing) {
            editName.setText(pizzaToEdit.getName());
            editIngredients.setText(pizzaToEdit.getIngredients());
            editPrice.setText(String.valueOf(pizzaToEdit.getPrice()));
            editSize.setText(String.valueOf(pizzaToEdit.getSize()));
            editDescription.setText(pizzaToEdit.getDescription());

            int statusPosition = statusAdapter.getPosition(pizzaToEdit.getStatus());
            if (statusPosition >= 0) {
                spinnerStatus.setSelection(statusPosition);
            }
        }

        // Тепер створюємо сам діалог
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(dialogTitle)
                .setView(dialogView)
                .setPositiveButton(positiveButtonText, null) // Обробник поки null
                .setNegativeButton("Скасувати", (dialogInterface, which) -> dialogInterface.dismiss())
                .create();

        // Цей код виконається, коли діалог вже готовий показатися.
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                String nameStr = editName.getText().toString().trim();
                String ingredientsStr = editIngredients.getText().toString().trim();
                String priceStr = editPrice.getText().toString().trim();
                String sizeStr = editSize.getText().toString().trim();
                String descriptionStr = editDescription.getText().toString().trim();
                String selectedStatus = spinnerStatus.getSelectedItem().toString();

                if (nameStr.isEmpty() || ingredientsStr.isEmpty() || priceStr.isEmpty() || sizeStr.isEmpty()) {
                    Toast.makeText(context, "Назва, інгредієнти, ціна та розмір є обов'язковими!", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    double price = Double.parseDouble(priceStr);
                    int size = Integer.parseInt(sizeStr); // Розмір піци, наприклад, в см

                    PizzaEntity pizzaToSave;
                    if (isEditing) {
                        pizzaToSave = pizzaToEdit;
                    } else {
                        pizzaToSave = new PizzaEntity();
                    }

                    pizzaToSave.setName(nameStr);
                    pizzaToSave.setIngredients(ingredientsStr);
                    pizzaToSave.setPrice(price);
                    pizzaToSave.setSize(size);
                    pizzaToSave.setDescription(descriptionStr);
                    pizzaToSave.setStatus(selectedStatus);

                    callback.onPizzaAdded(pizzaToSave);
                    dialog.dismiss();

                    // Повідомлення про успішне оновлення/додавання
                    if (isEditing) {
                        Toast.makeText(context, "Піцу оновлено успішно!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Піцу додано успішно!", Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Будь ласка, введіть правильні числа для ціни та розміру.", Toast.LENGTH_LONG).show();
                } catch (IllegalArgumentException e) {
                    Toast.makeText(context, "Помилка введення: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }
}