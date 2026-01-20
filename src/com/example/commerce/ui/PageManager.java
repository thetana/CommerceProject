package com.example.commerce.ui;

import java.util.HashMap;
import java.util.Map;

public class PageManager<T extends Page> {
    private Map<Class<T>, Page> oagePool = new HashMap<>();
    // 여기 저기서 사용 가능 하면서 하나만 존재 해야 하는데 제네릭 하기까지 해야해서 그냥 스테틱으로 만들면 컴파일 오류가 난다
    // 싱글턴을 이용해 인스턴스를 생성하지만 그 인스턴스가 스테틱한 특성을 갖게 했다
    private static final PageManager INSTANCE = new PageManager();

    private PageManager() {
        // 예외처리 안한다 여기서 실패하면 죽는게 낫다
        oagePool.put((Class<T>) SignPage.class, new SignPage());
        oagePool.put((Class<T>) MainPage.class, new MainPage());
        oagePool.put((Class<T>) CommercePage.class, new CommercePage());
        oagePool.put((Class<T>) CartPage.class, new CartPage());
        oagePool.put((Class<T>) OrderPage.class, new OrderPage());
        oagePool.put((Class<T>) AdminPage.class, new AdminPage());
//        oagePool.forEach(( k, v) -> System.out.println("페이지 풀 등록 완료 : " + v.name));
    }

    public static PageManager getInstance() {
        return INSTANCE;
    }
    public void startPage(Class<T> p){
        oagePool.get(p).start();
    }
    public String getName(Class<T> p){
        return oagePool.get(p).name;
    }
    public boolean isPageVisible(Class<T> p){
        return oagePool.get(p).isVisible;
    }
}
