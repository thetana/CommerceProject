package com.example.commerce.data;

/**
 * 고객 등급과 관련 된 이넘이다
 * 등급관련 필요한 정보를 다 보유하게 했다
 */
public enum Rank {
    BRONZE("BRONZE", 0, 0, "0%"),
    SILVER("SILVER", 0.05, 500000, "5%"),
    GOLD("GOLD", 0.1, 1000000, "10%"),
    PLATINUM("PLATINUM", 0.15, 2000000, "15%");


    private String name; // 이게 어떤 등급인지 보여줄 스트링
    private double rate; // 등급의 할인률
    private int amt; // 해당등급이 되려면 얼마를 써야 하는지
    private String des; // 할인률 표기

    Rank(String n, double r, int a, String d) {
        this.name = n;
        this.rate = r;
        this.amt = a;
        this.des = d;
    }

    public double getRate() {
        return rate;
    }

    public int getAmt() {
        return amt;
    }

    public String getDes() {
        return des;
    }

    @Override
    public String toString() {
        return name;
    }
}
