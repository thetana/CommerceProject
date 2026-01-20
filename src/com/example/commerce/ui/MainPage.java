package com.example.commerce.ui;

import java.util.Map;
import java.util.TreeMap;

import static java.lang.System.exit;

public class MainPage<T extends Page> extends Page {
    // Page 클래스의 이점을 만들어주는 라우터
    // 컬렉션에 내가 원하는 메뉴 페이지를 넣어주면 유저에게 보여주는 출력 부터 메뉴 선택까지 한줄 끝낼 수 있다
    // 새로운 페이지를 추가해도 동적으로 생성해준다
    private final Map<String, Class<T>> r = new TreeMap<>();

    public MainPage() {
        this.name = "[ 실시간 커머스 플랫폼 메인 ]";
        // 사용자한테 보여주고 이동 할 수 있는 페이지를 등록한다
        r.put("1", (Class<T>) CommercePage.class);
        r.put("2", (Class<T>) CartPage.class);
        r.put("3", (Class<T>) OrderPage.class);
        r.put("4", (Class<T>) AdminPage.class);
    }

    @Override
    public void start() {
        this.pm = PageManager.getInstance();
        boolean run = true;
        while (run) {
            System.out.println(this.name);
            // 이동 할 수 있는 메뉴들은 동적으로 생성하여 보여준다
            r.keySet().stream().filter(k -> pm.isPageVisible(r.get(k))).forEach((k) -> System.out.println(k + ". " + pm.getName(r.get(k))));
            System.out.println("0. 종료");
            String in = sc.next();
            if (in.equals("0") || in.equals("exit")) {
                run = false;
                exit(0);
            } else {
                try {
                    // if문 없이 맵을 이용해 유저가 선택한 메뉴로 바로 이동 한다
                    pm.startPage(r.get(in));
                } catch (NullPointerException e) {
                    System.out.println("입력하신 메뉴를 찾지 못했습니다. 다시 입력해 주세요.");
                }
            }
        }
    }
}
