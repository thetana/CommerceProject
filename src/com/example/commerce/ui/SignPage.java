package com.example.commerce.ui;

import com.example.commerce.api.CommerceSystem;

import java.io.Console;

import static java.lang.System.exit;

// 일단은 되게만 만들었는데 나중에 다시 손보자 이거 하느라 과제 조건 충족이 우선이다
public class SignPage extends Page {
    SignPage() {
        this.name = "로그인 회원가입";
    }

    @Override
    void start() {
        this.pm = PageManager.getInstance();
        boolean run = true;
        while (run) {
            System.out.println(this.name);
            System.out.println("1. 로그인");
            System.out.println("2. 회원가입");
            System.out.println("0. 종료");
            String in = sc.next();
            if (in.equals("0") || in.equals("exit")) {
                run = false;
                exit(0);
            } else {
                switch (in) {
                    case "1" -> signIn();
                    case "2" -> signUp();
                    default -> System.out.println("입력하신 메뉴를 찾지 못했습니다. 다시 입력해 주세요.");
                }
            }
        }
    }

    private void signIn() {
        System.out.println("[ 로그인 ]");
        String email = inputEmail(true);
        char[] pw = inputPw(true, email);
        if (CommerceSystem.setSignedEmail(email, pw)) {
            System.out.println("로그인에 성공 했습니다.");
            pm.notifyCartCountChanged();
            pm.startPage(MainPage.class);
        }
    }

    private void signUp() {
        System.out.println("[ 회원가입 ]");
        String email = inputEmail(false);
        char[] pw = inputPw(false, email);
        if (CommerceSystem.addCustomer(email, email, pw)) {
            System.out.println("회원가입에 성공 했습니다.");
        }
    }


    private String inputEmail(boolean isSignIn) { // 나중에 회원가입 할 때는 있는 아이디 인지 검사해야 한다
        System.out.println("아이디(이메일)을 입력해주세요: ");
        String email = sc.next();
        return email;
    }

    /**
     * 패스워드를 입력받을 때 사용하는 메소드 이다
     * 보안을 위해 운영체제 콘솔을 받아와 readPassword()를 실행하고 String 생성 없이 char[]을 바로 받는다
     * 만약 시스템 콘솔이 없으면 어쩔 수 없이 그냐 Scanner을 사용 하는데 IDE에서 실행하는 상황을 상정하고 분기했다
     * 배포 환경에서는 거의 운영체제 콘솔에 접근이 가능 할 것이라고 생각했고
     * 테스트 환경에서는 비밀번호가 보이거나 메모리 상에 남아 있는 것이 크게 문제 되지 않는다고 생각 했다
     */
    private char[] inputPw(boolean isSignIn, String id) {
        System.out.println("[ " + id + " ]");
        System.out.println("비밀번호를 입력해주세요: ");
        char[] pw = null;
        Console console = System.console();
        if (console != null) {
            pw = console.readPassword();
        } else {
            pw = sc.next().toCharArray();
        }
        return pw;
    }
}
