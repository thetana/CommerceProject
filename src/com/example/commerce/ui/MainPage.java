package com.example.commerce.ui;

import com.example.commerce.api.CommerceSystem;

import javax.swing.text.html.HTMLDocument;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.System.exit;

/**
 * 로그인에 성공하면 진입하는 메인Page이다
 * 사용자가 사용 할 기능을 큰틀에서 보여주고 선택 할 수 있게 한다
 * 요구조건과 최대한 비슷한 메뉴를 보여주기 위해 동적으로 생성하던 부분을 수정했다
 */
public class MainPage extends Page {

    public MainPage() {
        this.name = "[ 실시간 커머스 플랫폼 메인 ]";
    }

    @Override
    public void start() {
        this.pm = PageManager.getInstance();
        boolean run = true;
        while (run) {
            System.out.println(this.name);

            // 원래 요구조건은 카테고리를 여기서 바로 출력하는데 난 카테고리들을 보려면 한번 더 이동을 해야한다
            // 이렇게 만든 이유는 카테고리를 유저가 만들거나 줄이는 기능을 추가한다면 요구조건의 메뉴는 적절하지 않다고 판단해서 이다
            System.out.println("1. " + pm.getName(CommercePage.class));
            // 장바구니에 아이템이 있으면 보여준다
            if (pm.isPageVisible(CartPage.class)) { // PageManager가 Page들의 상태를 관리한다
                System.out.println("2. " + pm.getName(CartPage.class));
                System.out.println("3. 주문 취소");
            }
            System.out.println("4. " + pm.getName(AdminPage.class));
            System.out.println("5. 로그아웃");
            System.out.println("0. 종료");

            // 입력
            String in = sc.next();
            if (in.equals("0") || in.equals("exit") || in.equals("q")) {
                run = false; // 종료
                exit(0);
            } else if (in.equals("1")) {
                pm.startPage(CommercePage.class); // 장바구니에 상품 담기 위해 카테고리 보여줌
            } else if (in.equals("2") && pm.isPageVisible(CartPage.class)) {
                pm.startPage(CartPage.class); // 장바구니에 담긴 상품들 확인
            } else if (in.equals("3") && pm.isPageVisible(CartPage.class)) {
                if (selectYesOrNo("장바구니를 비우시겠습니까?")) {
                    CommerceSystem.removeCartAll(); // 장바구니 전체 삭제
                }
            } else if (in.equals("4")) {
                pm.startPage(AdminPage.class); // 관리자
            } else if (in.equals("5")) {
                // 로그아웃
                CommerceSystem.removeSignedEmail();
                run = false;
                break;
            } else System.out.println("입력하신 메뉴를 찾지 못했습니다. 다시 입력해 주세요.");
        }
    }

    /**
     * 장바구니에 있는 값을 전부 삭제 할 때 한번 물어보기 위해 사용한다
     * @param des 출력 할 내용
     */
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
