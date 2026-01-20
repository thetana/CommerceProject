package com.example.commerce.data;

import java.util.UUID;

public record Customer(String name, String email, String pw, byte[] salt, String rank) {
    public Customer(String name, String email, String pw, byte[] salt) {
        this(name, email, pw, salt, "");
    }

}
