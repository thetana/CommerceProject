package com.example.commerce.ui;

import com.example.commerce.api.CommerceSystem;

import javax.swing.text.html.HTMLDocument;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.System.exit;

public class MainPage<T extends Page> extends Page {


    public MainPage() {
        this.name = "[ 실시간 커머스 플랫폼 메인 ]";
    }

    @Override
    public void start() {
        this.pm = PageManager.getInstance();
        boolean run = true;
        while (run) {
            System.out.println(this.name);
            System.out.println("1. " + pm.getName(CommercePage.class));
            // 장바구니에 아이템이 있으면 보여준다
            if (pm.isPageVisible(CartPage.class)) {
                System.out.println("2. " + pm.getName(CartPage.class));
                System.out.println("3. 주문 취소");
            }
            System.out.println("4. " + pm.getName(AdminPage.class));
            System.out.println("5. 로그아웃");
            System.out.println("0. 종료");
            String in = sc.next();
            if (in.equals("0") || in.equals("exit") || in.equals("q")) {
                run = false;
                exit(0);
            } else if (in.equals("1")) {
                pm.startPage(CommercePage.class);
            } else if (in.equals("2") && pm.isPageVisible(CartPage.class)) {
                pm.startPage(CartPage.class);
            } else if (in.equals("3") && pm.isPageVisible(CartPage.class)) {
                if (selectYesOrNo("장바구니를 비우시겠습니까?")) {
                    CommerceSystem.removeCartAll();
                }
            } else if (in.equals("4")) {
                pm.startPage(AdminPage.class);
            } else if (in.equals("5")) {
                // 작동은 하는데 현재 로그인한 아이디 지워줘야 한다
                run = false;
                break;
            } else System.out.println("입력하신 메뉴를 찾지 못했습니다. 다시 입력해 주세요.");
        }
    }

    private boolean selectYesOrNo(String des) {
        while (true) {
            System.out.println(des);
            System.out.println("1. 확인    2. 취소");
            String in = sc.next();
            switch (in) {
                case "1" -> {
                    return true;
                }
                case "2" -> {
                    return false;
                }
                default -> System.out.println("잘못 입력 하였습니다.");
            }
        }
    }
}
