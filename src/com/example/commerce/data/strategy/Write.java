package com.example.commerce.data.strategy;

import java.util.Map;

@FunctionalInterface
public interface Write <T extends Map, U>{
    boolean apply (T database, U data);
}
