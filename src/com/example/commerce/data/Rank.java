package com.example.commerce.data;

public enum Rank {
    BRONZE("BRONZE", 0, 0, "0%"),
    SILVER("SILVER", 0.05, 500000, "5%"),
    GOLD("GOLD", 0.1, 1000000, "10%"),
    PLATINUM("PLATINUM", 0.15, 2000000, "15%");


    private String name;
    private double rate;
    private int amt;
    private String des;

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
