package com.example.commerce.api;

import com.example.commerce.data.Category;
import com.example.commerce.data.Customer;
import com.example.commerce.data.DataManager;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Map;

public class CommerceSystem {
    // 현재 로그인한 유저 세션쯤의 역할이다
    private static String signedEmail;

    public static Map<String, Category> getCategorys() {
        return DataManager.read(DataManager.CATEGORYS);
    }

    public static boolean addCustomer(String name, String email, char[] pw) {
        byte[] salt = generateSalt();
        Customer c = new Customer(name, email, hash(pw, salt), salt);
        return DataManager.write(DataManager.CUSTOMERS, c);
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
