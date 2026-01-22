package com.example.commerce.data.model;


import com.example.commerce.data.Rank;

/**
 * 패스워드를 입력받을 때 사용하는 메소드 이다
 */
public record Customer(String id, String name, String pw, byte[] salt, Rank rank, int totalAmount) implements HasId {
    public Customer(String id, String name, String pw, byte[] salt) {
        this(id, name, pw, salt, Rank.BRONZE,0);
    }

}
