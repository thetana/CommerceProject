package com.example.commerce.ui;

import com.example.commerce.api.CommerceSystem;
import com.example.commerce.data.model.Customer;
import com.example.commerce.data.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 장바구니에 담은 상품들을 보여주고 최종 주문을 하거나 장바구니에서 지우고 싶은 상품을 선택해서 지운다
 */

class CartPage extends Page {
    CartPage() {
        this.name = "장바구니 확인";
    }

    @Override
    void start() {
        this.pm = PageManager.getInstance();
        boolean run = true;
        while (run) {
            // 카트안에 들어있는 상품 목록을 가져온다
            List<Product> list = CommerceSystem.getCart();
            // 카트안에 담겨 있는 상품 총액이 계산된 값을 가져온다 여기서 계산해도 무방한데 이전에 계산 함수 만들었어서 그냥 가져왔다
            int price = CommerceSystem.getCartPrice();
            // 재고 변경 정보를 출력하기 위해서 재고 데이터를 가져온다
            Map<String, Product> oldStocks = new HashMap<>();
            list.forEach(cart -> {
                oldStocks.put(cart.id(), CommerceSystem.getProduct(cart.categoryId(), cart.id()));
            });
            Customer me = CommerceSystem.getCustomer(); // 할인률 관련 내용을 적용하기 위해 현재 유저를 가져온다

            // 출력
            System.out.println("아래와 같이 주문 하시겠습니까?");
            System.out.println("아니면 특정 상품만 제거 해도됨!(●'◡'●)");
            System.out.println();
            System.out.println("[ 장바구니 내역 ]");
            list.forEach(System.out::println);
            System.out.println();
            System.out.println("[ 총 주문 금액 ]");
            System.out.println(price + "원");
            System.out.println(me.rank() + " 등급 이므로 " + me.rank().getDes() + " 할인이 적용됩니다. ");
            System.out.println(me.rank() + " 등급 할인(" + me.rank().getDes() + "): -" + (price * me.rank().getRate()) + "원");
            System.out.println("최종 결제 금액: " + (price - (price * me.rank().getRate())) + "원");
            System.out.println("1. 주문 확정  2. 특정 상품 제거  0. 메인으로 돌아가기");

            // 입력
            String in = sc.next();
            if (in.equals("1")) { // 주문 완료 하기
                if (CommerceSystem.setRank()) {
                    System.out.println("주문이 완료되었습니다! 총 금액: " + (price - (price * me.rank().getRate())) + "원");
                    list.forEach(cart -> {
                        Product newStock = CommerceSystem.getProduct(cart.categoryId(), cart.id());
                        System.out.println(cart.name() + " 재고가 " + oldStocks.get(cart.id()).count() + "개 → " + newStock.count() + "개로 업데이트되었습니다.");
                    });
                    run = false;
                    break;
                } else {
                    System.out.println("주문 실패 했습니다.");
                }
            } else if (in.equals("2")) { // 장바구니 상품 제거 하기
                String name = inputName("제거 할 상품명을 입력해주세요: ");
                if (CommerceSystem.removeCart(name)) {
                    System.out.println(name + "는 이제 장바구니에 없어. ヾ(•ω•`)o");
                    // 제거 후 주문을 하거나 계속 제거 하고 싶을 것 같으니 뒤로 보내지 않는다
                } else {
                    System.out.println("제거 할 상품을 찾지 못했어!! (；′⌒`)");
                }

            } else if (in.equals("0") || in.equals("exit")) { // 뒤로가기
                run = false;
                break;
            } else {
                System.out.println("입력하신 메뉴를 찾지 못했습니다. 다시 입력해 주세요.");
            }
        }
    }

    /**
     * 지울 상품을 이름으로 찾아온다
     * @param des 유저한테 보여줄 내용을 받아온다
     */
    private String inputName(String des) {
        System.out.print(des);
        sc.nextLine(); // 이전 next가 남긴 개행 제거
        String in = sc.nextLine();
        return in;
    }
}
