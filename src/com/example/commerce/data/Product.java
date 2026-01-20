package com.example.commerce.data;

import java.util.UUID;

public record Product(UUID Uuid, String name, int price, String note, int count) {
    public Product(String name, int price, String note, int count) {
        this(UUID.randomUUID(), name, price, note, count);
    }



    @Override
    public String toString() {
        return name + " | " + price + "원 | " + note + " | 재고: " + count + "개";
    }
}
