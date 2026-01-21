package com.example.commerce.api;

import com.example.commerce.data.DataManager;
import com.example.commerce.data.model.Category;
import com.example.commerce.data.model.Customer;
import com.example.commerce.data.model.Product;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class CommerceSystem {
    // 현재 로그인한 유저 세션쯤의 역할이다
    private static String signedEmail;

    public static Map<String, Category> getCategorys() {
        return DataManager.read(DataManager.CATEGORYS);
    }

    public static boolean addCustomer(String email, String name, char[] pw) {
        byte[] salt = generateSalt();
        Customer data = new Customer(email, name, hash(pw, salt), salt);
        return DataManager.write(DataManager.CUSTOMERS, data);
    }

    public static boolean setSignedEmail(String email, char[] pw) {
        Map<String, Customer> customers = DataManager.read(DataManager.CUSTOMERS);
        boolean isOk = false;
        try {
            Customer customer = customers.get(email);
            if (customer.pw().equals(hash(pw, customer.salt()))) {
                signedEmail = email;
                isOk = true;
            } else {
                isOk = false;
            }

        } catch (NullPointerException e) {
            isOk = false;
        }
        return isOk;
    }

    public static String getSignedEmail() {
        return signedEmail;
    }

    public static boolean addCart(String categoryId, String productid, int cnt) {
        Product product = getProducts(categoryId).stream().filter(p -> p.id().equals(productid)).findFirst().orElseThrow();
        if(product.count() < cnt){
            return false;
        }
        Product data = new Product(product.id(), product.name(), product.price(), product.note(), cnt);
        return DataManager.write(DataManager.CARTS, data);
    }

    public static List<Product> getProducts(String categoryId) {
        return DataManager.readList(DataManager.PRODUCTS, categoryId);
    }
    public static int getProductCount(String categoryId, String productid) {
        return getProducts(categoryId).stream().filter(p -> p.id().equals(productid)).findFirst().orElseThrow().count();
    }


    private static String hash(char[] password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, 100000, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NullPointerException e) {
            // 여기서 로그를 찍어 놓자
            throw new RuntimeException(e);
        }
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}
