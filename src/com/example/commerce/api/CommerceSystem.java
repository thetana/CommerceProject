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

    public static Product getProduct(String categoryId, String productid) {
        return getProducts(categoryId).stream().filter(p -> p.id().equals(productid)).findFirst().orElseThrow();
    }

    public static int getProductCount(String categoryId, String productid) {
        return getProducts(categoryId).stream().filter(p -> p.id().equals(productid)).mapToInt(Product::count).sum();
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
        String rank = "BRONZE";
        if (total < 500000) {
            rank = "BRONZE";
        } else if (total >= 500000 && total < 1000000) {
            rank = "SILVER";
        } else if (total >= 1000000 && total < 2000000) {
            rank = "GOLD";
        } else if (total >= 2000000) {
            rank = "PLATINUM";
        } else {
            rank = "BRONZE";
        }
        Customer updated = new Customer(customer.id(), customer.name(), customer.pw(), customer.salt(), rank, total);

        // 트렌잭션은 없지만 그래도 할 수 있는 만큼 데이터 안꼬이게 처리 하면서 하자
        if(DataManager.write(DataManager.CUSTOMERS, updated)){
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
