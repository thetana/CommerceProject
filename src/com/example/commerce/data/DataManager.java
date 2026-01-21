package com.example.commerce.data;

import com.example.commerce.api.CommerceSystem;
import com.example.commerce.data.model.*;
import com.example.commerce.data.strategy.Write;

import java.util.*;

/*
범용화된 데이터 관리 클래스를 만들려는 시도가 있었다
다만 데이터를 정형화 시키면서 요구사항을 충족시키는 것에서 다음과 같은 어려움이 있었고 그로인해 경험을 통해 배운 것으로 만족하기로 했다
 - 카테고리가 리스트를 보유해야 한다는 요구 조건을 충족시키면서 진행 하고자 했다
 - 카테고리는 리스트를 보유하긴 하지만 기획적으로 절대적인 1 대 다 관계는 아니다 (언젠가 다 대 다 관계로 변경해도 이상할게 없다)
 - 1 대 다 관계의 데이터를 관리 하더라고 카테고리 클래스에 상품 리스트를 맴버변수로 넣는 구조는 범용화 시키기 아렵다
 - 데이터는 하나의 자료구조, 또는 테이블에 저장 되고 관계를 따로 저장 하거나 각 데이터가 자신의 소속을 표시하게 하는 구조가 범용화에 유리하다
 (내가 일반적으로 경험한 구조이기도 하다)
 - 만약 카테고리가 리스트를 보유해야한다면 상속을 활용해 어느정도 범용화가 가능 할지도 모른다는 생각은 든다
 - 모든게 RDBMS를 사용하면 문제가 해결 되지만 더 실력 향상에 도움이 될 수 있는 방향을 생각해봤을 때 지금 RDBMS는 좋은 선택이 아니라고 생각했다
 - 처음부터 요구조건이 절대 적인지 협의가 가능한지 정하고 그에 따라 최종 확정된 요구조건에 맞춰서 설계가 들어갔어야 했다
 */

/*
원래 만들고자 한 구조
 - 데이터는 HasId라는 id 필드를 명시하는 인터페이스를 상속 받아서 recode로 만든다
 - 데이터 recode 종류별로 1차원 자료구조를 만들고 저장한다 (가능하면 TreeMap에 저장 하길 원한다)
 - 데이터의 소유 (유저의 주문내역등)는 각 데이터의 필드로 보유해준다
 - 범용적인 메소드들만 만들고 인자로 어떤 데이터를 테이블을 지정 하듯 내가 데이터를 가져 올 자료구조를 지정하는 방식을 취한다
 */



/**
 * 프로젝트에 데이터 저장을 총괄 한다
 * ORM DB 클라이언트 쯤으로 보면 좋을 것 같은데 외부에서는 구체적인 저장이 어떤 저장소를 사용하는지 (자바 자료구조인지, SQL인지, 파일베이스인지 등등)
 * 몰라도 원하는 CRUD를 하도록 하는 것이 목적이다
 * 서버의 DB와 클라이언트의 로컬 저장소의 역할을 모두 수행 한다
 * (서버가 없는데 무슨 말인가 싶을 수도 있는데 현 프로젝트는 서버에서 담당 할 로직과 클라이언트에서 담당할 로직을 가능한 분리 해서 관리 하고 있다)
 */
public class DataManager {
    private static final Map<String, Map> tables = new HashMap<>();
    private static final Map<String, Category> categorys = new TreeMap<>();
    private static final Map<String, Customer> customers = new TreeMap<>();
    private static final Map<String, ArrayList<Product>> carts = new TreeMap<>(); // 유저 email을 키로하는 유저별 장바구니
    private static final Map<String, ArrayList<Order>> orders = new TreeMap<>(); // 유저 email을 키로하는 유저별 장바구니
    public static final String CATEGORYS = "categorys";
    public static final String CUSTOMERS = "customers";
    public static final String CARTS = "carts";

    static {
        init();
    }

    private static void init() {
        // 테이블 빨리 찾으려고 해쉬맵에 넣어놓자
        tables.put(CATEGORYS, categorys);
        tables.put(CUSTOMERS, customers);
        tables.put(CARTS, carts);

        // 데이터 초기화
        List<Product> products;
        products = new ArrayList<>();
        products.add(new Product("Galaxy S25", 1200000, "최신 안드로이드 스마트폰", 25));
        products.add(new Product("iPhone 15", 1350000, "Apple의 최신 스마트폰", 30));
        products.add(new Product("MacBook Pro", 2400000, "M3 칩셋이 탑재된 노트북", 15));
        products.add(new Product("AirPods Pro", 350000, "노이즈 캔슬링 무선 이어폰", 50));
        putCategory("1", "전자제품", products);
        products = new ArrayList<>();
        putCategory("2", "의류", products);
        products = new ArrayList<>();
        putCategory("3", "식품", products);
    }

    public static Map read(String what) {
        switch (what) {
            case CATEGORYS -> {
                return Map.copyOf(categorys);
            }
            case CUSTOMERS -> {
                return Map.copyOf(customers);
            }
            default -> {
                return null;
            }
        }
    }


    public static boolean write(String what, Object data) {
        boolean isOk = false;
        switch (what) {
            case CATEGORYS -> {
                isOk = true;
            }
            case CUSTOMERS -> {
                if (data instanceof Customer) {
                    Customer customer = (Customer) data;
                    customers.put(customer.id(), customer);
                    isOk = true;
                } else {
                    isOk = false;
                }
            }
            case CARTS -> {
                if (data instanceof Product) {
                    Product product = (Product) data;
                    carts.get(CommerceSystem.getSignedEmail()).add(product);
                    customers.put(customer.email(), customer);
                    isOk = true;
                } else {
                    isOk = false;
                }
            }
            default -> {
                isOk = false;
            }
        }

        return isOk;
    }

    private static void putCategory(String key, String name, List<Product> products) {
        categorys.put(key, new Category(name, products));
    }
}
