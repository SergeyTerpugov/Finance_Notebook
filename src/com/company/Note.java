package com.company;

import java.util.Objects;

public class Note {
    private final int price;
    private final String description;

    public Note(int price, String description) {
        this.price = price;
        this.description = Objects.requireNonNull(description, "description").trim();
    }

    public static Note parse(String raw) {
        int sep = raw.indexOf(':');
        if (sep <= 0) {
            throw new IllegalArgumentException("Ожидается формат 'Цена:Описание', получено: " + raw);
        }
        int price;
        try {
            price = Integer.parseInt(raw.substring(0, sep).trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Цена должна быть целым числом: " + raw, e);
        }
        String description = raw.substring(sep + 1).trim();
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Описание не может быть пустым: " + raw);
        }
        return new Note(price, description);
    }

    public int getPrice() { return price; }
    public String getDescription() { return description; }

    public String toRaw() { return price + ":" + description; }
}