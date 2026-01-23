package com.example.commerce.data.model;

import java.util.List;

/**
 * 카테고리가 상품 목록 리스트를 보유하는 것은 적절한 구조라고 생각하진 않는다
 * 다만 요구사항을 지키는데 최선을 다하고 싶었다
 */
public record Category(String id, String name, List<Product> products) implements HasId {

    @Override
    public String toString() {
        return name;
    }
}
