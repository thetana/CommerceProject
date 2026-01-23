package com.example.commerce.ui;

import com.example.commerce.api.CommerceSystem;
import com.example.commerce.data.model.Customer;

import java.io.Console;

import static java.lang.System.exit;

/**
 * 로그인과 회원가입을 담당하는 Page이다
 * 아이디와 비밀번호 입력에 대한 메소드를 보유하고 있다
 */
public class SignPage extends Page {
    SignPage() {
        this.name = "로그인 회원가입";
    }

    @Override
    void start() {
        this.pm = PageManager.getInstance();
        boolean run = true;
        while (run) {
            // 출력
            System.out.println(this.name);
            System.out.println("1. 로그인");
            System.out.println("2. 회원가입");
            System.out.println("0. 종료");

            // 입력
            String in = sc.next();
            if (in.equals("0") || in.equals("exit")) {
                run = false;
                exit(0);
            } else {
                switch (in) {
                    case "1" -> signIn(); // 로그인
                    case "2" -> signUp(); // 회원 가입
                    default -> System.out.println("입력하신 메뉴를 찾지 못했습니다. 다시 입력해 주세요.");
                }
            }
        }
    }

    /**
     * 로그인을 진행하는 메소드 이다
     * inputEmail()과 inputPw()를 호출해서 사용자 입력을 받고 받아온 값으로 CommerceSystem.setSignedEmail 이용해서 회원 가입을 진행 한다
     */
    private void signIn() {
        System.out.println("[ 로그인 ]");
        String email = inputEmail(false); // 이메일 입력하기
        if (email == null || email.isBlank()) { // ID 값이 있나 검증
            System.out.println("이메일 입력에 실패 했습니다.");
            return;
        }
        char[] pw = inputPw(false, email); // 비밀번호 입력하기
        if (pw == null || pw.length == 0) { // 비밀번호 값이 있나 검증
            System.out.println("비밀번호 입력에 실패 했습니다.");
            return;
        }
        if (CommerceSystem.setSignedEmail(email, pw)) { // 로그인 진행
            System.out.println("로그인에 성공 했습니다.");
            pm.notifyCartCountChanged();
            pm.startPage(MainPage.class);
        }else{
            System.out.println("로그인에 실패 했습니다.");
        }
    }

    /**
     * 회원 가입을 진행하는 메소드 이다
     * inputEmail()과 inputPw()를 호출해서 사용자 입력을 받고 받아온 값으로 CommerceSystem.addCustomer를 이용해서 회원 가입을 진행 한다
     */
    private void signUp() {
        System.out.println("[ 회원가입 ]");
        String email = inputEmail(true); // 이메일 입력하기
        if (email == null || email.isBlank()) { // ID 값이 있나 검증
            System.out.println("이메일 입력에 실패 했습니다.");
             return;
        }
        char[] pw = inputPw(true, email); // 비밀번호 입력하기
        if (pw == null || pw.length == 0) { // 비밀번호 값이 있나 검증
            System.out.println("비밀번호 입력에 실패 했습니다.");
            return;
        }
        if (CommerceSystem.addCustomer(email, email, pw)) { // 회원 가입 진행
            System.out.println("회원가입에 성공 했습니다.");
        }else{
            System.out.println("회원가입에 실패 했습니다.");
        }
    }

    /**
     * 이메일을 입력 받는 메소드 이다
     * 이메일이 입력 되었는지 정규식으로 검증을 하는 로직이 필요 할 것 같다
     * 이미 가입된 이메일이 있는지 체크하고 있다
     * @param isSignUp 회원 가입 할 때 필요한 분기를 하기위해 보유하고 있다
     * @return 사용자가 입력한 값을 리턴한다 null을 반환하면 실패 상황이다
     */
    private String inputEmail(boolean isSignUp) {
        String email = null;
        boolean run = true;
        while (run) { // 실패시 바로 다시 입력 할 수 있게 하기
            System.out.println("아이디(이메일)을 입력해주세요: ");
            email = sc.next();
            if (email == null || email.isBlank()) System.out.println("이메일 입력에 실패 했습니다."); // 빈 값이 들어오진 않았나
            if (isSignUp && CommerceSystem.getCustomer(email) != null) { // 이미 아이디가 존재 하는가
                System.out.println(email + "는 이미 가입된 아이디 입니다");
                email = null;
            }else{ // 존재하는 아이디가 없으면 입력한 아이디를 넘긴다
                break;
            }
        }
        return email;
    }

    /**
     * 패스워드를 입력받을 때 사용하는 메소드 이다
     * 보안을 위해 운영체제 콘솔을 받아와 readPassword()를 실행하고 String 생성 없이 char[]을 바로 받는다
     * 만약 시스템 콘솔이 없으면 어쩔 수 없이 그냐 Scanner을 사용 하는데 IDE에서 실행하는 상황을 상정하고 분기했다
     * 배포 환경에서는 거의 운영체제 콘솔에 접근이 가능 할 것이라고 생각했고
     * 테스트 환경에서는 비밀번호가 보이거나 메모리 상에 남아 있는 것이 크게 문제 되지 않는다고 생각 했다
     * @param isSignUp 회원 가입 할 때 필요한 분기를 하기위해 보유하고 있다
     * @param id 입력받은 id(email)을 보여주기 위해서 보유하고 있다
     * @return 사용자가 입력한 값을 리턴한다 null을 반환하면 실패 상황이다
     */
    private char[] inputPw(boolean isSignUp, String id) {
        System.out.println("[ " + id + " ]");
        System.out.println("비밀번호를 입력해주세요: ");
        char[] pw = null;
        Console console = System.console(); // 시스템 콘솔을 받아온다
        if (console != null) { // IDE면 시스템 콘솔이 없다
            pw = console.readPassword(); // 시스템 콘솔이 있으면 안전하게 비밀번호 입력을 받는다
        } else {
            pw = sc.next().toCharArray(); // IDE이면 그냥 입력 받는다
        }
        return pw;
    }
}
