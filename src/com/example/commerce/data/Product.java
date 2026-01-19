package com.example.commerce.data;

public record Product(String name, int price, String note, int count) {
    @Override
    public String toString() {
        return name + " | " + price + "원 | " + note + " | 재고: " + count + "개";
    }
}
