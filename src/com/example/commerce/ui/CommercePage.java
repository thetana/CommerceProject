package com.example.commerce.ui;

class CommercePage extends Page{
    CommercePage(){
        this.name = "상품 구매";
        this.pm = PageManager.getInstance();
    }

    @Override
    void start() {
        this.pm = PageManager.getInstance();
        boolean run = true;
        while (run) {
            System.out.println(this.name);
            // 이동 할 수 있는 메뉴들은 동적으로 생성하여 보여준다
            r.keySet().stream().filter(k -> pm.isPageVisible(r.get(k))).forEach((k) -> System.out.println(k + ". " + pm.getName(r.get(k))));
            System.out.println("0. 종료");
            String in = sc.next();
            if (in.equals("0") || in.equals("exit")) {
                run = false;
                System.exit(0);
            } else {
                try {
                    // if문 없이 맵을 이용해 유저가 선택한 메뉴로 바로 이동 한다
                    pm.startPage(r.get(in));
                } catch (NullPointerException e) {
                    System.out.println("입력하신 메뉴를 찾지 못했습니다. 다시 입력해 주세요.");
                }
            }
        }
    }
}
