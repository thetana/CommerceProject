package com.example.commerce;

import com.example.commerce.ui.PageManager;
import com.example.commerce.ui.SignPage;

public class Main {
    public static void main(String[] args) {
        PageManager.getInstance().startPage(SignPage.class);
    }
}