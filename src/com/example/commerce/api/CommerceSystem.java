package com.example.commerce.api;

import com.example.commerce.data.DataManager;
import com.example.commerce.data.Rank;
import com.example.commerce.data.model.Category;
import com.example.commerce.data.model.Customer;
import com.example.commerce.data.model.Product;
import com.example.commerce.ui.PageManager;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class CommerceSystem {
    // 현재 로그인한 유저 세션쯤의 역할이다
    private static String signedEmail;
    private static PageManager pm = PageManager.getInstance();
    private static byte[] adminSalt = generateSalt();
    //    private static String adminPw = hash(new char[]{'a', 'd', 'm', 'i', 'n', '1', '2', '3'}, adminSalt);
    private static String adminPw = hash(new char[]{'1', '2', '3'}, adminSalt);


    //region 상품 관련
    public static Map<String, Category> getCategorys() {
        return DataManager.read(DataManager.CATEGORYS);
    }

    public static List<Product> getProducts(String categoryId) {
        return DataManager.readList(DataManager.PRODUCTS, categoryId);
    }

    public static Product getProduct(String categoryId, String productid) {
        return getProducts(categoryId).stream().filter(p -> p.id().equals(productid)).findFirst().orElseThrow();
    }

    public static Product getProductByName(String name) {
        Product product = null;
        Map<String, Category> map = getCategorys();
        for (Category c : map.values()) {
            try {
                product = c.products().stream().filter(p -> p.name().equals(name)).findFirst().orElseThrow();
            } catch (NoSuchElementException e) {
            }
        }
        return product;
    }

    public static Product getProductById(String id) {
        Product product = null;
        Map<String, Category> map = getCategorys();
        for (Category c : map.values()) {
            try {
                product = c.products().stream().filter(p -> p.id().equals(id)).findFirst().orElseThrow();
            } catch (NoSuchElementException e) {
            }
        }
        return product;
    }

    public static int getProductCount(String categoryId, String productid) {
        return getProducts(categoryId).stream().filter(p -> p.id().equals(productid)).mapToInt(Product::count).sum();
    }

    public static boolean setdProuctStock(String categoryId, String productid, int cnt) {
        boolean isOk = false;
        Product old = getProduct(categoryId, productid);
        Product updated = new Product(old.id(), old.categoryId(), old.name(), old.price(), old.note(), old.count() - cnt);

        // 트렌잭션은 없지만 그래도 할 수 있는 만큼 데이터 안꼬이게 처리 하면서 하자
        if (DataManager.write(DataManager.PRODUCTS, updated)) {
            isOk = true;
        }
        return isOk;
    }
    //endregion

    //region 장바구니 관련
    public static boolean addCart(String categoryId, String productid, int cnt) {
        boolean isOk = false;
        Product product = getProducts(categoryId).stream().filter(p -> p.id().equals(productid)).findFirst().orElseThrow();
        if (checkProductCount(categoryId, productid, cnt)) {
            int cartCnt = getProductCountInCart(productid);
            Product data = new Product(product.id(), product.categoryId(), product.name(), product.price(), product.note(), cnt + cartCnt);
            isOk = DataManager.write(DataManager.CARTS, data);
            if (isOk) {
                pm.notifyCartCountChanged();
            }
        }
        return isOk;
    }

    public static List<Product> getCart() {
        return DataManager.readList(DataManager.CARTS, signedEmail);
    }

    public static int getCartCount() {
        return getCart().size();
    }

    public static int getCartPrice() {
        return getCart().stream().mapToInt(value -> value.price() * value.count()).sum();
    }

    public static boolean isSetProduct(String productid) {
        return getCart().stream().anyMatch(p -> p.id().equals(productid));
    }

    public static int getProductCountInCart(String productid) {
        return getCart().stream().filter(p -> p.id().equals(productid)).mapToInt(Product::count).sum();
    }

    public static boolean checkProductCount(String categoryId, String productid, int cnt) {
        boolean isOk = false;
        int stock = getProducts(categoryId).stream().filter(p -> p.id().equals(productid)).mapToInt(Product::count).sum();
        int cart = getProductCountInCart(productid);
        isOk = stock >= (cnt + cart);
        return isOk;
    }

    public static boolean removeCartAll() {
        boolean isOk = false;
        isOk = DataManager.remove(DataManager.CARTS);
        return isOk;
    }

    public static boolean setRank() {
        boolean isOk = false;
        Map<String, Customer> customers = DataManager.read(DataManager.CUSTOMERS);
        Customer customer = customers.get(signedEmail);

        int total = customer.totalAmount() + getCartPrice();
        Rank rank = Rank.BRONZE;
        if (total >= Rank.PLATINUM.getAmt()) {
            rank = Rank.PLATINUM;
        } else if (total >= Rank.GOLD.getAmt()) {
            rank = Rank.GOLD;
        } else if (total >= Rank.SILVER.getAmt()) {
            rank = Rank.SILVER;
        } else {
            rank = Rank.BRONZE;
        }
        Customer updated = new Customer(customer.id(), customer.name(), customer.pw(), customer.salt(), rank, total);

        // 트렌잭션은 없지만 그래도 할 수 있는 만큼 데이터 안꼬이게 처리 하면서 하자
        if (DataManager.write(DataManager.CUSTOMERS, updated)) {
            List<Product> cart = getCart();
            cart.forEach(p -> setdProuctStock(p.categoryId(), p.id(), p.count()));
            isOk = removeCartAll();
        }

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
    //endregion

    //region 관리자 관련
    public static boolean isAdmin(char[] pw) {
        boolean isOk = false;
        if (adminPw.equals(hash(pw, adminSalt))) {
            isOk = true;
        } else {
            isOk = false;
        }
        return isOk;
    }

    public static boolean addProduct(String categoryId, String name, int price, String note, int count) {
        boolean isOk = false;
        Product data = new Product(categoryId, name, price, note, count);
        isOk = DataManager.write(DataManager.PRODUCTS, data);
        return isOk;
    }

    public static boolean modProduct(String categoryId, String productid, String name, int price, String note, int count) {
        boolean isOk = false;
        Product data = new Product(productid, categoryId, name, price, note, count);
        isOk = DataManager.write(DataManager.PRODUCTS, data);
        return isOk;
    }

    public static boolean delProduct(String productid) {
        boolean isOk = false;
        isOk = DataManager.remove(DataManager.PRODUCTS, productid);
        return isOk;
    }


    //endregion

    //region 유틸 관련
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
