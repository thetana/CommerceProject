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
        products.put("1", new Product("Galaxy S25", 1200000, "최신 안드로이드 스마트폰", 25));
        products.put("2", new Product("iPhone 15", 1350000, "Apple의 최신 스마트폰", 30));
        products.put("3", new Product("MacBook Pro", 2400000, "M3 칩셋이 탑재된 노트북", 15));
        products.put("4", new Product("AirPods Pro", 350000, "노이즈 캔슬링 무선 이어폰", 50));
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
