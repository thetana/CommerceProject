package com.example.commerce.ui;

import com.example.commerce.api.CommerceSystem;
import com.example.commerce.data.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CartPage extends Page {
    CartPage() {
        this.name = "장바구니 확인";
    }

    @Override
    void start() {
        this.pm = PageManager.getInstance();
        boolean run = true;
        while (run) {
            List<Product> list = CommerceSystem.getCart();
            int price = CommerceSystem.getCartPrice();
            Map<String, Product> oldStocks = new HashMap<>();
            list.forEach(cart -> {
                oldStocks.put(cart.id(), CommerceSystem.getProduct(cart.categoryId(), cart.id()));
            });

            System.out.println("아래와 같이 주문 하시겠습니까?");
            System.out.println();
            System.out.println("[ 장바구니 내역 ]");
            list.forEach(System.out::println);
            System.out.println();
            System.out.println("[ 총 주문 금액 ]");
            System.out.println(price + "원");

            System.out.println("1. 주문 확정      2. 메인으로 돌아가기");
            String in = sc.next();
            if (in.equals("1")) {
                if (CommerceSystem.setRank()) {
                    System.out.println("주문이 완료되었습니다! 총 금액: " + price + "원");
                    list.forEach(cart -> {
                        Product newStock = CommerceSystem.getProduct(cart.categoryId(), cart.id());
                        System.out.println(cart.name() + " 재고가 " + oldStocks.get(cart.id()).count() + "개 → " + newStock.count() + "개로 업데이트되었습니다.");
                    });
                } else {
                    System.out.println("주문 실패 했습니다.");
                }
            } else if (in.equals("2") || in.equals("exit")) {
                run = false;
                break;
            } else {
                System.out.println("입력하신 메뉴를 찾지 못했습니다. 다시 입력해 주세요.");
            }
        }
    }
}
