package com.example.commerce.data;

import java.util.*;

/**
 * 프로젝트에 데이터 저장(상태관리)을 총괄 합니다.
 * 서버의 DB와 클라이언트의 로컬 저장소의 역할을 모두 수행 합니다
 * (서버가 없는데 무슨 말인가 싶을 수도 있는데 현 프로젝트는 서버에서 담당 할  로직과 클라이언트에서 담당할 로직을 가능한 분리 해서 관리 하고 있습니다)
 */
public class DataManager {
    private static final Map<String, Category> categorys = new TreeMap<>();
    private static final Map<String, Customer> customers = new TreeMap<>();
    public static final String CATEGORYS = "categorys";
    public static final String CUSTOMERS = "customers";

    static {
        init();
    }

    private static void init() {
        List<Product> products;
        products = new ArrayList<>();
        products.add(new Product("Galaxy S25", 1200000, "최신 안드로이드 스마트폰", 25));
        products.add(new Product("iPhone 15", 1350000, "Apple의 최신 스마트폰", 30));
        products.add(new Product("MacBook Pro", 2400000, "M3 칩셋이 탑재된 노트북", 15));
        products.add(new Product("AirPods Pro", 350000, "노이즈 캔슬링 무선 이어폰", 50));
        putCategory("1", "전자제품", products);
        products = new ArrayList<>();
        putCategory("2", "의류", products);
        products = new ArrayList<>();
        putCategory("3", "식품", products);
    }

    public static Map read(String name) {
        switch (name) {
            case CATEGORYS -> {
                return Map.copyOf(categorys);
            }
            case CUSTOMERS -> {
                return Map.copyOf(customers);
            }
            default -> {
                return null;
            }
        }
    }

    // 오브젝트 스트림을 쓴다면 많이 변경 해야 할 듯
    public static boolean write(String name, Object data) {
        boolean isOk = false;
        switch (name) {
            case CATEGORYS -> {
                isOk = true;
            }
            case CUSTOMERS -> {
                if (data instanceof Customer) {
                    Customer customer = (Customer) data;
                    customers.put(customer.email(), customer);
                    isOk = true;
                }else{
                    isOk = false;
                }
            }
            default -> {
                isOk = false;
            }
        }

        return isOk;
    }

    private static void putCategory(String key, String name, List<Product> products) {
        categorys.put(key, new Category(name, products));
    }
}
