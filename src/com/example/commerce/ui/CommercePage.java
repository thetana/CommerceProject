package com.example.commerce.ui;

import com.example.commerce.api.CommerceSystem;
import com.example.commerce.data.model.Category;
import com.example.commerce.data.model.Product;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 상품을 장바구니에 담는 절차를 담당 한다 진행 순서는 다음과 같다
 * 1. 카테고리 선택
 * 2. selectFilter (어떤 기준으로 조회 할지)
 * 3. selectCategory (상품 목록을 보여주고 장바구니에 담을 상품을 선택한다)
 * 4. selectProduct (선택된 상품을 몇개 담을지 수량 입력)
 * 메소드들 명칭은 좀 하는 동작이랑 잘 매치가 안된다는 생각이 드는데 시간이 있다면 이름을 다시 지어보자
 */
class CommercePage extends Page {

    CommercePage() {
        this.name = "상품 구매";
        this.pm = PageManager.getInstance();
    }

    @Override
    void start() {
        this.pm = PageManager.getInstance();
        // 카테고리 들을 조회한다
        Map<String, Category> map = new TreeMap<>(CommerceSystem.getCategorys());
        boolean run = true;
        while (run) {
            // 유저가 입력 할 값이 키인 카테고리를 선택 할 수 있게 보여준다
            map.keySet().forEach((k) -> System.out.println(k + ". " + map.get(k)));
            System.out.println("0. 뒤로가기");
            String in = sc.next();
            if (in.equals("0") || in.equals("exit")) {
                run = false;
                break;
            } else {
                try {
                    // 유저가 입력한 값을 키로 카테고리를 선택한다
                    selectFilter(map.get(in));
                    break;
                } catch (NullPointerException e) {
                    System.out.println("입력하신 메뉴를 찾지 못했습니다. 다시 입력해 주세요.");
                }
            }
        }
    }

    /**
     * 어떤 필터 기준으로 조회 할지 선택 한다
     * @param category 이전에 선택한 카테고리를 넘겨준다
     */
    private void selectFilter(Category category) {
        boolean run = true;
        while (run) {
            // 출력
            System.out.println("[ " + category + " 카테고리 ]");
            System.out.println("1. 전체 상품 보기");
            System.out.println("2. 가격대별 필터링 (100만원 이하)");
            System.out.println("3. 가격대별 필터링 (100만원 초과)");
            System.out.println("0. 뒤로가기");

            //선택
            String in = sc.next();
            switch (in) {
                case "0", "exit", "q" -> {
                    run = false;
                    break;
                }
                // 카테고리 안에 상품 목록을 필터에 맞게 가공해서 넘겨준다
                case "1" -> {
                    selectCategory(category.id(), category.products(), "[ 전체 상품 목록 ]");
                }
                case "2" -> {
                    selectCategory(category.id(), category.products().stream().filter(p -> p.price() <= 1000000).toList(), "[ 100만원 이하 상품 목록 ]");
                }
                case "3" -> {
                    selectCategory(category.id(), category.products().stream().filter(p -> p.price() > 1000000).toList(), "[ 100만원 초과 상품 목록 ]");
                }
            }
        }
    }

    /**
     * 조회된 상품 목록에서 장바구니에 담을 상품을 선택한다
     * @param categoryId 이전에 선택한 카테고리 ID
     * @param list 필터링조건에 맞는 상품 목록
     * @param des 유저한테 보여줄 문자열 (설명)
     */
    private void selectCategory(String categoryId, List<Product> list, String des) {
        boolean run = true;
        while (run) {
            System.out.println(des);
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
                // 유저가 입력한 상품을 넘겨준다 리스트에 인덱스를 이용해 선택 인터페이스를 정했다
                selectProduct(categoryId, list.get(Integer.parseInt(in) - 1));
                break;
            }
        }
    }

    /**
     * 상품의 개수를 입력받아서 장바구니에 담는다
     * @param categoryId 이전에 선택한 카테고리 ID
     * @param product 유저가 선택한 상품이다
     */
    private void selectProduct(String categoryId, Product product) {
        System.out.println(product);
        int count = inputCount(categoryId, product.id());
        if (count > 0) {
            // 개수가 양수면 담아라
            CommerceSystem.addCart(categoryId, product.id(), count);
        }else{
            System.out.println("0 보다 큰 값만 입력 가능 합니다");
        }
    }

    /**
     * 상품의 개수를 입력받아서 장바구니에 담는다
     * @param categoryId 이전에 선택한 카테고리 ID
     * @param productid 상품의 아이디를 보낸다 (이 프로그램에서 오브젝트는 유일하지 않다 id가 유니크한 속성이다)
     */
    private int inputCount(String categoryId, String productid) {
        boolean run = true;
        int cnt = -1;
        while (run) {
            System.out.println("담을 수량을 입력해 주세요. (0 입력 : 뒤로가기)");
            System.out.print("수량 : ");
            try {
                cnt = sc.nextInt();
                if (cnt == 0) { // 0으로 뒤로가기 하자
                    run = false;
                    break;
                } else if (cnt < 0) { // 음수 인가
                    System.out.println("0 보다 큰 값만 입력 가능 합니다");
                }  else if (CommerceSystem.checkProductCount(categoryId, productid, cnt)) { // 재고 확인
                    run = false;
                    break;
                } else { // 이프 순서 바꾸면 꼬인다
                    System.out.println("재고가 부족합니다");
                }
            }catch (InputMismatchException e){
                System.out.println("숫자만 입력 가능 합니다");
                cnt = -1;
                sc.next(); // 버퍼에 남은 값을 제거해준다
            }
        }
        return cnt;
    }
}