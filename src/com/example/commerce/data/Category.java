package com.example.commerce.data;

import java.util.Map;

public record Category(String name, Map<String, Product> products) {


}
