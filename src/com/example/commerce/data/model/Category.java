package com.example.commerce.data.model;

import java.util.List;

public record Category(String id, String name, List<Product> products) implements HasId {


    @Override
    public String toString() {
        return name;
    }
}
