package com.example.commerce.ui;

import com.example.commerce.api.CommerceSystem;
import com.example.commerce.data.Category;
import com.example.commerce.data.Product;

import java.util.Map;
import java.util.TreeMap;

class CommercePage extends Page {

    CommercePage() {
        this.name = "상품 구매";
        this.pm = PageManager.getInstance();
    }

    @Override
    void start() {
        this.pm = PageManager.getInstance();
        Map<String, Category> map = new TreeMap<>(CommerceSystem.getCategorys());
        boolean run = true;
        while (run) {
            // 선택 할 수 있게 보여준다
            map.keySet().forEach((k) -> System.out.println(k + ". " + map.get(k).name()));
            System.out.println("0. 뒤로가기");
            String in = sc.next();
            if (in.equals("0") || in.equals("exit")) {
                run = false;
                break;
            } else {
                try {
                    // if문 없이 맵을 이용해 유저가 선택한 메뉴로 바로 이동 한다
                    selectCategory(map.get(in).products());
                    break;
                } catch (NullPointerException e) {
                    System.out.println("입력하신 메뉴를 찾지 못했습니다. 다시 입력해 주세요.");
                }
            }
        }
    }

    public void selectCategory(Map<String, Product> map) {
        this.pm = PageManager.getInstance();
        boolean run = true;
        while (run) {
            // 선택 할 수 있게 보여준다
            map.keySet().forEach((k) -> System.out.println(k + ". " + map.get(k)));
            System.out.println("0. 뒤로가기");
            String in = sc.next();
            if (in.equals("0") || in.equals("exit")) {
                run = false;
                break;
            } else {
                try {
                    // if문 없이 맵을 이용해 유저가 선택한 메뉴로 바로 이동 한다
                    selectProduct(in, map.get(in));
                    break;
                } catch (NullPointerException e) {
                    System.out.println("입력하신 메뉴를 찾지 못했습니다. 다시 입력해 주세요.");
                }
            }
        }
    }

    public void selectProduct(String key, Product product) {
        System.out.println(product);
    }


}
