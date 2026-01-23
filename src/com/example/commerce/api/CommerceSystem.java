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

/**
 * 백앤드 쯤의 역할을 한다 비즈니스 로직은 여기 다 있다
 * 도메인에 따라 코드 분리를 해주는게 맞다고 생각하는데 시간 나면 하자
 * DataManager는 여기서만 사용하는게 원칙인데 DataManager를 맴버변수로 보유하게 할걸 그랬다 싶다
 * 그러면 CommerceSystem (또는 다른 System이 만들어지면 다른 System들도)에서만 데이터 가공을 한다는 것을 명시 할 수 있을텐데
 * 로직을 보다보면 의문이 들 수 있는 부분이 데이터를 직접적으로 수정하지 않는다
 * 원래 취지는 데이터 저장소에서 데이터를 수정하고 수정된 데이터를 조회해서 사용하자 여서 모든 데이터 클래스들이 record이다
 * 따라서 수정은 없고 기존 오브젝트를 복하하여 변경된 값이 반영된 새 오브젝트를 만들고 기존 오브젝트를 지워준다
 */
public class CommerceSystem {
    // 현재 로그인한 유저 세션쯤의 역할이다
    private static String signedEmail;
    // 필요한 상태값 변화를 PageManager에게 알려주기 위해 사용
    private static PageManager pm = PageManager.getInstance();
    // 어드민 관련
    private static byte[] adminSalt = generateSalt();
    private static String adminPw = hash(new char[]{'1', '2', '3'}, adminSalt);
    //    private static String adminPw = hash(new char[]{'a', 'd', 'm', 'i', 'n', '1', '2', '3'}, adminSalt);

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

    // 카테고리 전체를 조회해서 이름으로 조회해서 찾는다
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

    // 카테고리 아이디를 못쓸 때 카테고리 전체를 조회해서 아이디로 상품을 찾아온다
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

    public static boolean setProuctStock(String categoryId, String productid, int cnt) {
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

    public static int getProductCountInCart(String productid) {
        return getCart().stream().filter(p -> p.id().equals(productid)).mapToInt(Product::count).sum();
    }

    public static boolean checkProductCount(String categoryId, String productid, int cnt) {
        boolean isOk = false;
        int stock = getProductCount(categoryId, productid);
        int cart = getProductCountInCart(productid);
        isOk = stock >= (cnt + cart);
        return isOk;
    }

    public static boolean removeCartAll() {
        boolean isOk = false;
        isOk = DataManager.remove(DataManager.CARTS);
        pm.notifyCartCountChanged();
        return isOk;
    }

    public static boolean removeCart(String name) {
        boolean isOk = false;
        try {
            isOk = DataManager.remove(DataManager.CARTS, getCart().stream().filter(p -> p.name().equals(name)).findFirst().orElseThrow().id());
            pm.notifyCartCountChanged();
        } catch (NoSuchElementException e) {
        }
        return isOk;
    }

    // 이게 주문 이다 이름을 좀 잘못 지은 것 같다
    public static boolean setRank() {
        boolean isOk = false;
        Customer customer = getCustomer();

        int total = (int) (customer.totalAmount() + (getCartPrice() - (getCartPrice() * customer.rank().getRate())));
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
        // 저장 되면 데이터순서가 마지막으로 바뀐다 수정대신 재생성을 해서 그런건데 record를 안쓰던 순서를 고정하던 처리를 해야한다
        if (DataManager.write(DataManager.CUSTOMERS, updated)) {
            List<Product> cart = getCart();
            cart.forEach(p -> setProuctStock(p.categoryId(), p.id(), p.count()));
            isOk = removeCartAll();
        }

        return isOk;
    }
    //endregion

    //region 회원 관련
    // 회원 가입이다
    public static boolean addCustomer(String email, String name, char[] pw) {
        boolean isOk = false;
        if(getCustomer(email) == null){
            byte[] salt = generateSalt();
            Customer data = new Customer(email, name, hash(pw, salt), salt);
            isOk = DataManager.write(DataManager.CUSTOMERS, data);
        }else{
            // 이미 회원 가입이 되어 있다 메세지를 보내주면 좋긴 할텐데 시간이 있다면 메세지 양식을 정의해보자
            isOk = false;
        }
        return isOk;
    }
    // 로그인 이다
    public static boolean setSignedEmail(String email, char[] pw) {
        boolean isOk = false;
        try {
            Customer customer = getCustomer(email);
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

    // 로그아웃 이다
    public static void removeSignedEmail() {
        signedEmail = null;
    }

    // 특정 유저를 가져 올 때 주로 로그인 전에 사용함
    public static Customer getCustomer(String id) {
        Map<String, Customer> customers = DataManager.read(DataManager.CUSTOMERS);
        return customers.get(id);
    }

    // 현재 로그인한 유저를 가져 올 때
    public static Customer getCustomer() {
        return getCustomer(getSignedEmail());
    }
    //endregion

    //region 관리자 관련
    // 관리자 인증
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
