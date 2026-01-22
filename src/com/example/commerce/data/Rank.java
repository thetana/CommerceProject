package com.example.commerce.data;

public enum Rank {
    BRONZE(0, 0), SILVER(0.05, 500000), GOLD(0.1, 1000000), PLATINUM(0.15, 2000000);


    private double rate;
    private int amt;

    Rank(double r, int a) {
        this.rate = r;
        this.amt = a;
    }

    public double getRate() {
        return rate;
    }
    public int getAmt() {
        return amt;
    }

}
