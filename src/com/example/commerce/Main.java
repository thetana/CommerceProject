package com.example.commerce;

import com.example.commerce.ui.MainPage;
import com.example.commerce.ui.PageManager;

public class Main {
    public static void main(String[] args) {
        PageManager.getInstance().startPage(MainPage.class);
    }
}