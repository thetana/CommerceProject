package com.example.commerce.ui;

import com.example.commerce.api.CommerceSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * Page들을 총괄 한다 필요에 따라 상태 값을 변경하기도 하고 페이지 생성과 페이지 사용을 이 클래스가 수행한다
 * 싱글턴으로 만들어져 있는데 제네릭을 사용하니까 그냥 스태틱으로 했을 떄 컴파일 에러가나서 싱글턴으로 했다
 */
public class PageManager<T extends Page> {
    // 키로 클래스 자체를 받고 있는데 페이지는 클래스로 존재하고 페이지를 다른 곳에 등록 해서 사용하는 절차를 없이 특정지을 수 있어서 이렇게 했다
    // 만약 익명클래스로 페이지가 만들어지고 그걸 pool에서 관리해야 한다면 좀 곤란한 상황이지만
    // 오히려 익명 클래스 사용을 제한 하는 룰이기도 하다 Page라는게 익명으로 만들어서 쓰라고 만든게 아니다
    // 익명으로 만들어서 쓰더라도 풀에서 관리하기 보단 1회성 사용 후 참조가 끊어 지게 하는게 좋을 것 같다
    private Map<Class<T>, Page> oagePool = new HashMap<>();
    // 여기 저기서 사용 가능 하면서 하나만 존재 해야 하는데 제네릭 하기까지 해야해서 그냥 스테틱으로 만들면 컴파일 오류가 난다
    // 싱글턴을 이용해 인스턴스를 생성하지만 그 인스턴스가 스테틱한 특성을 갖게 했다
    private static final PageManager INSTANCE = new PageManager();

    private PageManager() {
        oagePool.put((Class<T>) SignPage.class, new SignPage());
        oagePool.put((Class<T>) MainPage.class, new MainPage());
        oagePool.put((Class<T>) CommercePage.class, new CommercePage());
        oagePool.put((Class<T>) CartPage.class, new CartPage());
        oagePool.put((Class<T>) AdminPage.class, new AdminPage());
    }

    public static PageManager getInstance() {
        return INSTANCE;
    }

    /**
     * Page들을 시작한다
     * @param p 실행 할 페이지의 클래스
     */
    public void startPage(Class<T> p) {
        oagePool.get(p).start();
    }

    /**
     * 메뉴 같은 곳에서 페이지 이름 보여주려고 만들었다
     * @param p 실행 할 페이지의 클래스
     */
    public String getName(Class<T> p) {
        return oagePool.get(p).name;
    }

    /**
     * 페이지의 보이고 안보이고 상태이다
     * @param p 실행 할 페이지의 클래스
     */
    public boolean isPageVisible(Class<T> p) {
        return oagePool.get(p).isVisible;
    }

    /**
     * 페이지 보이고 안보이고 설정
     * @param p 실행 할 페이지의 클래스
     */
    private void setPageVisible(Class<T> p, boolean isVisible) {
        oagePool.get(p).isVisible = isVisible;
    }

    /**
     * 외부에서 PageManager에게 무언가 변경사항을 알려줄 때 notify를 호출해서 알려주고
     * 뭔가 처리를 해줘야 한다면 PageManager에서 확인 할 것을 확인하고 처리를 한다
     */
    public void notifyCartCountChanged() {
        if (CommerceSystem.getCartCount() > 0) {
            setPageVisible((Class<T>) CartPage.class, true);
        } else {
            setPageVisible((Class<T>) CartPage.class, false);
        }
    }
}
