package kolokviumski2;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

enum COMPARATOR_TYPE {
    NEWEST_FIRST,
    OLDEST_FIRST,
    LOWEST_PRICE_FIRST,
    HIGHEST_PRICE_FIRST,
    MOST_SOLD_FIRST,
    LEAST_SOLD_FIRST
}

class ProductNotFoundException extends Exception {
    ProductNotFoundException(String message) {
        super(message);
    }
}


class Product {
    private String category;
    private String id;
    private LocalDateTime createdAt;
    private double price;
    private int quantitySold;

    public Product(String category, String id, LocalDateTime createdAt, double price) {
        this.category = category;
        this.id = id;
        this.createdAt = createdAt;
        this.price = price;
        quantitySold = 0;
    }

    public double buy(int quantity) {
        quantitySold+=quantity;
        return price*quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "category='" + category + '\'' +
                ", id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", price=" + price +
                ", quantitySold=" + quantitySold +
                '}';
    }
}


class OnlineShop {
    Map<String, Product> products;
    Map<String, List<Product>> productsByCategory;
    OnlineShop() {
        products = new HashMap<>();
        productsByCategory = new TreeMap<>();
    }

    void addProduct(String category, String id, String name, LocalDateTime createdAt, double price){
        Product toadd = new Product(category,id,createdAt,price);
        products.put(id,toadd);
        productsByCategory.putIfAbsent(category,new ArrayList<>());
        productsByCategory.get(category).add(toadd);
    }

    double buyProduct(String id, int quantity) throws ProductNotFoundException{
        if (products.get(id)==null)
            throw new ProductNotFoundException(String.format("Product with id %s does not exist in the online shop!",id));
        return products.get(id).buy(quantity);
    }

    List<List<Product>> listProducts(String category, COMPARATOR_TYPE comparatorType, int pageSize) {
        List<Product> productsList = (category==null)?new ArrayList<>(products.values()):productsByCategory.get(category);
//        Comparator<Product> comparator =
        List<List<Product>> result = new ArrayList<>();
        return result;
    }

}

public class OnlineShopTest {

    public static void main(String[] args) {
        OnlineShop onlineShop = new OnlineShop();
        double totalAmount = 0.0;
        Scanner sc = new Scanner(System.in);
        String line;
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] parts = line.split("\\s+");
            if (parts[0].equalsIgnoreCase("addproduct")) {
                String category = parts[1];
                String id = parts[2];
                String name = parts[3];
                LocalDateTime createdAt = LocalDateTime.parse(parts[4]);
                double price = Double.parseDouble(parts[5]);
                onlineShop.addProduct(category, id, name, createdAt, price);
            } else if (parts[0].equalsIgnoreCase("buyproduct")) {
                String id = parts[1];
                int quantity = Integer.parseInt(parts[2]);
                try {
                    totalAmount += onlineShop.buyProduct(id, quantity);
                } catch (ProductNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                String category = parts[1];
                if (category.equalsIgnoreCase("null"))
                    category=null;
                String comparatorString = parts[2];
                int pageSize = Integer.parseInt(parts[3]);
                COMPARATOR_TYPE comparatorType = COMPARATOR_TYPE.valueOf(comparatorString);
                printPages(onlineShop.listProducts(category, comparatorType, pageSize));
            }
        }
        System.out.println("Total revenue of the online shop is: " + totalAmount);

    }

    private static void printPages(List<List<Product>> listProducts) {
        for (int i = 0; i < listProducts.size(); i++) {
            System.out.println("PAGE " + (i + 1));
            listProducts.get(i).forEach(System.out::println);
        }
    }
}

