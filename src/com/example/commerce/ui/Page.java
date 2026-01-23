package com.example.commerce.ui;

import java.util.Scanner;

/**
 * 이 프로젝트에서 입출력을 담당한다 프론트앤드에서 할법한 것들을 Page들이 담당한다
 * 각 Page 클래스 에서 Page에서 해야 할 일들을 정의하고 Page들에 상태값 관리나 Page 호출등은 PageManager가 담당한다
 * Page들은 PageManager에서 Page pool이라는 곳 에서 관리하고 있다 단일 오브젝트로 존재하며 처음 프로그램 실행 시 각 Page들을 생성한다
 * PageManager에서 초기 생성 한 후 다른 곳에서 Page를 생성 할 수 없다
 * 개발을 진행하다 보니 컨포넌트 같은 것들이 생겨났다 후에 여력이 되면 컨포넌트도 클래스로 정의 하고 싶다
 */
abstract class Page {
    Scanner sc = new Scanner(System.in);
    PageManager pm;
    String name;
    boolean isVisible = true;

    /**
     * 페이지를 실행 한다
     */
    abstract void start();
}
