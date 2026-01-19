package com.example.commerce.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DataManager {
    private static final Map<String, Category> categorys = new TreeMap<>();
    public static final String CATEGORYS = "categorys";

    static {
        init();
    }

    private static void init() {
        Map<String, Product> products;
        products = new TreeMap<>();
        putCategory("1", "전자제품", products);
        products = new TreeMap<>();
        putCategory("2", "의류", products);
        products = new TreeMap<>();
        putCategory("3", "식품", products);
    }

    public static Map read(String name) {
        switch (name) {
            case CATEGORYS -> {
                return Map.copyOf(categorys);
            }
            default -> {
                return null;
            }
        }
    }

    private static void putCategory(String key, String name, Map<String, Product> products) {
        categorys.put(key, new Category(name, products));
    }
}
