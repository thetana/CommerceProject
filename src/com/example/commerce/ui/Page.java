package com.example.commerce.ui;

import java.util.Scanner;

abstract class Page {
    Scanner sc = new Scanner(System.in);
    PageManager pm;
    String name;
    boolean isVisible = true;

    abstract void start();
}
