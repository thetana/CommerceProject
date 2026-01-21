package com.example.commerce.api;

import com.example.commerce.data.DataManager;
import com.example.commerce.data.model.Category;
import com.example.commerce.data.model.Customer;
import com.example.commerce.data.model.Product;
import com.example.commerce.ui.PageManager;

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
    private static PageManager pm = PageManager.getInstance();

    //region 상품 관련
    public static Map<String, Category> getCategorys() {
        return DataManager.read(DataManager.CATEGORYS);
    }

    public static List<Product> getProducts(String categoryId) {
        return DataManager.readList(DataManager.PRODUCTS, categoryId);
    }

    public static int getProductCount(String categoryId, String productid) {
        return getProducts(categoryId).stream().filter(p -> p.id().equals(productid)).findFirst().orElseThrow().count();
    }
    //endregion

    //region 장바구니 관련
    public static boolean addCart(String categoryId, String productid, int cnt) {
        boolean isOk = false;
        Product product = getProducts(categoryId).stream().filter(p -> p.id().equals(productid)).findFirst().orElseThrow();
        if (checkProductCount(categoryId, productid, cnt)) {
            int cartCnt = getProductCountInCart(productid);
            Product data = new Product(product.id(), product.name(), product.price(), product.note(), cnt + cartCnt);
            isOk = DataManager.write(DataManager.CARTS, data);
            if (isOk) {
                pm.notifyCartCountChanged();
            }
        }
        return isOk;
    }

    public static List<Product> getCarts() {
        return DataManager.readList(DataManager.CARTS, signedEmail);
    }

    public static int getCartCount() {
        return getCarts().size();
    }

    public static boolean isSetProduct(String productid) {
        return getCarts().stream().anyMatch(p -> p.id().equals(productid));
    }

    public static int getProductCountInCart(String productid) {
        return getCarts().stream().filter(p -> p.id().equals(productid)).mapToInt(Product::count).sum();
    }

    public static boolean checkProductCount(String categoryId, String productid, int cnt) {
        boolean isOk = false;
        int stock = getProducts(categoryId).stream().filter(p -> p.id().equals(productid)).mapToInt(Product::count).sum();
        int cart = getProductCountInCart(productid);
        isOk = stock >= (cnt + cart);
        return isOk;
    }
    //endregion

    //region 회원 관련
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
    //endregion
}
