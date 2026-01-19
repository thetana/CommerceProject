package com.example.commerce.api;

import com.example.commerce.data.Category;
import com.example.commerce.data.DataManager;
import com.example.commerce.ui.MainPage;

import java.util.Map;

public class CommerceSystem {

    public static Map<String, Category> getCategorys() {
        return DataManager.getCategorys();
    }


}
