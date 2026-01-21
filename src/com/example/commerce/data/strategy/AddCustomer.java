package com.example.commerce.data.strategy;

import com.example.commerce.data.model.Customer;

import java.util.Map;
import java.util.TreeMap;

public class AddCustomer implements Write<Map<String, Customer>, Customer>{

    @Override
    public boolean apply(Map<String, Customer> database, Customer data) {
        return false;
    }
}
