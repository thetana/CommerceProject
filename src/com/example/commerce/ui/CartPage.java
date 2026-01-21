package com.example.commerce.ui;

import com.example.commerce.api.CommerceSystem;
import com.example.commerce.data.model.Category;
import com.example.commerce.data.model.Product;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class CartPage extends Page {
    CartPage() {
        this.name = "장바구니 확인";
    }

    @Override
    void start() {
        this.pm = PageManager.getInstance();
        boolean run = true;
        while (run) {
            System.out.println("아래와 같이 주문 하시겠습니까?");
            System.out.println();
            System.out.println("[ 장바구니 내역 ]");
            List<Product> list = CommerceSystem.getCarts();
            list.forEach(System.out::println);
            System.out.println();
            System.out.println("[ 총 주문 금액 ]");
            System.out.println(list.stream().mapToInt(value -> value.price() * value.count()).sum() + "원");

            System.out.println("1. 주문 확정      2. 메인으로 돌아가기");
            String in = sc.next();
            if (in.equals("2") || in.equals("exit")) {
                run = false;
                break;
            } else {
                try {
                    break;
                } catch (NullPointerException e) {
                    System.out.println("입력하신 메뉴를 찾지 못했습니다. 다시 입력해 주세요.");
                }
            }
        }
    }
}
