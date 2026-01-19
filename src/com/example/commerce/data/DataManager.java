package com.example.commerce.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DataManager {
    private static final Map<String, Category> categorys = new TreeMap<>();

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
        putCategory("3", "의류", products);
    }

    public static Map<String, Category> getCategorys() {
        return Map.copyOf(categorys);
    }

    private static void putCategory(String key, String name, Map<String, Product> products) {
        categorys.put(key, new Category(name, products));
    }
}
