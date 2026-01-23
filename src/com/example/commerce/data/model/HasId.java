package com.example.commerce.data.model;

/**
 * 데이터들의 id보유 여부를 강제화 한다
 * 영구저장 상황을 상정해서 record로 모든 데이터를 정의 했다 (실수 였다)
 */
public interface HasId {
    String id();
}
