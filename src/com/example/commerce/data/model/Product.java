package com.example.commerce.data.model;

import java.util.UUID;

public record Product(String id, String categoryId, String name, int price, String note, int count) implements HasId {
    public Product(String categoryId, String name, int price, String note, int count) {
        this(UUID.randomUUID().toString(), categoryId, name, price, note, count);
    }

    @Override
    public String toString() {
        return name + " | " + price + "원 | " + note + " | 재고: " + count + "개";
    }
}
