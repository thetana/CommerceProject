package com.example.commerce.ui;

import com.example.commerce.api.CommerceSystem;
import com.example.commerce.data.model.Category;
import com.example.commerce.data.model.Product;

import java.io.Console;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static java.lang.System.exit;

class AdminPage extends Page {
    private boolean isPass = false;
    private int life = 3;

    AdminPage() {
        this.name = "관리자 모드";
    }

    @Override
    void start() {
        // 매번 비밀번호 치고 들어오게 하자
        isPass = false;
        life = 3;


        boolean run = true;
        while (run) {
            // 인증했나 확인한다
            if (!isPass) {
                // 인증 안했으면 비밀번호 입력하게 한다
                if (CommerceSystem.isAdmin(inputPw())) {
                    // 맞으면 통과
                    isPass = true;
                } else {
                    // 틀리면 목숨을 하나 까고 다시 입력의 기회를 준다
                    life--;
                    if (life <= 0) {
                        return;
                    } else {
                        continue;
                    }
                }
            }
            // 혹시 모르니 한번 더 확인하고 기능 실행 하자
            if (isPass) {
                // 각 메뉴를 page로 만들어 주는 것이 적절해 보이긴 하는데 우선 그냥 했다
                System.out.println("[ " + name + " ]");
                System.out.println("1. 상품 추가");
                System.out.println("2. 상품 수정");
                System.out.println("3. 상품 삭제");
                System.out.println("4. 전체 상품 현황");
                System.out.println("0. 메인으로 돌아가기");
                String in = sc.next();

                switch (in) {
                    case "0", "exit" -> {
                        run = false;
                        break;
                    }
                    case "1" -> {
                        addProduct();
                    }
                    case "2" -> {
                        modProduct();
                    }
                    case "3" -> {
                        delProduct();
                    }
                    default -> System.out.println("입력하신 메뉴를 찾지 못했습니다. 다시 입력해 주세요.");
                }
            }
        }
    }

    private void addProduct() {
        Category category = selectCategory("어느 카테고리에 상품을 추가하시겠습니까?");
        System.out.println("[ " + category + " 카테고리에 상품 추가 ]");
        String name = inputName("상품명을 입력해주세요: ");
        int price = inputPrice("가격을 입력해주세요: ");
        String note = inputNote("상품 설명을 입력해주세요: ");
        int count = inputCount("재고수량을 입력해주세요: ");

        // 이건 프로덕트 toString 양식이랑 같은데 프로덕트를 생성하는 일은 Page에선 하지 않기 때문에 따로 적어준다
        if (selectYesOrNo(name + " | " + price + "원 | " + note + " | 재고: " + count + "개", "위 정보로 상품을 추가하시겠습니까?")) {
            CommerceSystem.addProduct(category.id(), name, price, note, count);
            System.out.println("상품이 성공적으로 추가되었습니다!");
        }
    }

    private void modProduct() {
        Product product = null;
        boolean run = true;
        while (run) {
            if (product == null) {
                String name = inputName("수정할 상품명을 입력해주세요: ");
                product = CommerceSystem.getProductByName(name);
                if (product == null) {
                    System.out.println("상품을 찾지 못했습니다");
                    continue;
                }
            }
            int price = product.price();
            String note = product.note();
            int count = product.count();
            String msg = ""; // 분기를 2번 안하거나 분기별로 수정 로직을 따로 작성 하지 않거나 하기 위해서 메세지 저장용으로 쓴다
            System.out.println("현재 상품 정보: " + product);
            System.out.println();
            System.out.println("수정할 항목을 선택해주세요:");
            System.out.println("1. 가격");
            System.out.println("2. 설명");
            System.out.println("3. 재고수량");
            String in = sc.next();
            switch (in) {
                case "0", "exit" -> {
                    run = false;
                    break;
                }
                case "1" -> {
                    System.out.println("현재 가격: " + product.price() + "원");
                    price = inputPrice("새로운 가격을 입력해주세요: ");
                    msg = product.name() + "의 가격이 " + product.price() + "원 → " + price + "원으로 수정되었습니다.";
                }
                case "2" -> {
                    System.out.println("현재 설명  : " + product.note());
                    note = inputNote("새로운 설명을 입력해주세요: ");
                    msg = product.name() + "의 설명이 " + product.note() + " → " + note + "으로 수정되었습니다.";
                }
                case "3" -> {
                    System.out.println("현재 재고수량  : " + product.count() + "개");
                    count = inputCount("새로운 재고수량을 입력해주세요: ");
                    msg = product.name() + "의 재고수량이 " + product.count() + "개 → " + count + "개로 수정되었습니다.";
                }
                default -> System.out.println("입력하신 항목을 찾지 못했습니다. 다시 입력해 주세요.");
            }
            if(CommerceSystem.modProduct(product.categoryId(), product.id(), product.name(), price, note, count)){
                System.out.println(msg);
                break;
            }else{
                System.out.println("수정이 실패 했습니다.");
            }

        }
    }

    private void delProduct() {
        Category category = selectCategory("어느 카테고리에 상품을 삭제하시겠습니까?");
        System.out.println("[ " + category + " 카테고리에 상품 삭제 ]");
        List<Product> list = category.products();
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ". " + list.get(i));
        }

        String in = sc.next();
        Product product = list.get(Integer.parseInt(in) - 1);
        if (selectYesOrNo(product.toString(), "위 상품을 삭제하시겠습니까?")) {
            CommerceSystem.delProduct(product.id());
            System.out.println("상품이 성공적으로 삭제되었습니다!");
        }
    }


    private String inputName(String des) {
        System.out.print(des);
        sc.nextLine(); // 이전 next가 남긴 개행 제거
        String in = sc.nextLine();
        return in;
    }

    private int inputPrice(String des) {
        System.out.print(des);
        int in = sc.nextInt();
        return in;
    }

    private String inputNote(String des) {
        System.out.print(des);
        sc.nextLine(); // 이전 nextInt가 남긴 개행 제거
        String in = sc.nextLine();
        return in;
    }

    private int inputCount(String des) {
        System.out.print(des);
        int in = sc.nextInt();
        return in;
    }

    private Category selectCategory(String des) {
        System.out.println(des);
        Map<String, Category> map = new TreeMap<>(CommerceSystem.getCategorys());
        map.keySet().forEach((k) -> System.out.println(k + ". " + map.get(k)));
        return map.get(sc.next());
    }

    private boolean selectYesOrNo(String info, String des) {
        while (true) {
            System.out.println(info);
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

    /**
     * 패스워드를 입력받을 때 사용하는 메소드 이다
     * 보안을 위해 운영체제 콘솔을 받아와 readPassword()를 실행하고 String 생성 없이 char[]을 바로 받는다
     * 만약 시스템 콘솔이 없으면 어쩔 수 없이 그냥 Scanner을 사용 하는데 IDE에서 실행하는 상황을 상정하고 분기했다
     * 배포 환경에서는 거의 운영체제 콘솔에 접근이 가능 할 것이라고 생각했고
     * 테스트 환경에서는 비밀번호가 보이거나 메모리 상에 남아 있는 것이 크게 문제 되지 않는다고 생각 했다
     */
    private char[] inputPw() {
        System.out.println("관리자 비밀번호를 입력해주세요: ");
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
