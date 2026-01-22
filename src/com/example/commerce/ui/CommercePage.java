package com.example.commerce.ui;

import com.example.commerce.api.CommerceSystem;
import com.example.commerce.data.model.Category;
import com.example.commerce.data.model.Product;

import java.util.List;
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
            map.keySet().forEach((k) -> System.out.println(k + ". " + map.get(k)));
            System.out.println("0. 뒤로가기");
            String in = sc.next();
            if (in.equals("0") || in.equals("exit")) {
                run = false;
                break;
            } else {
                try {
                    // if문 없이 맵을 이용해 유저가 선택한 메뉴로 바로 이동 한다
                    selectCategory(in, map.get(in).products());
                    break;
                } catch (NullPointerException e) {
                    System.out.println("입력하신 메뉴를 찾지 못했습니다. 다시 입력해 주세요.");
                }
            }
        }
    }

    private void selectCategory(String categoryId, List<Product> list) {
        this.pm = PageManager.getInstance();
        boolean run = true;
        while (run) {
            // 선택 할 수 있게 보여준다
            for (int i = 0; i < list.size(); i++) {
                System.out.println((i + 1) + ". " + list.get(i));
            }
            System.out.println("0. 뒤로가기");
            String in = sc.next();
            if (in.equals("0") || in.equals("exit")) {
                run = false;
                break;
            } else {
                selectProduct(categoryId, list.get(Integer.parseInt(in) - 1));
                break;
            }
        }
    }

    private void selectProduct(String categoryId, Product product) {
        System.out.println(product);
        int count = inputCount(categoryId, product.id());
        if (count > 0) {
            CommerceSystem.addCart(categoryId, product.id(), count);
        }
    }

    private int inputCount(String categoryId, String productid) {
        boolean run = true;
        int cnt = -1;
        while (run) {
            System.out.println("담을 수량을 입력해 주세요. (0 입력 : 뒤로가기)");
            System.out.print("수량 : ");
            cnt = sc.nextInt();
            if (cnt == 0) {
                run = false;
                break;
            } else if (CommerceSystem.checkProductCount(categoryId, productid, cnt)) {
                break;
            } else {
                System.out.println("재고가 부족합니다");
            }
        }
        return cnt;
    }
}