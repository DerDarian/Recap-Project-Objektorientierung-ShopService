import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

@AllArgsConstructor
public class ShopService {
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;

    public ShopService() {
        this.productRepo = new ProductRepo(new ArrayList<>());
        this.orderRepo = new OrderMapRepo();
    }

    public Order addOrder(String id, List<String> productIds) {
        List<Product> products = new ArrayList<>();
        for (String productId : productIds) {
            Optional<Product> productToOrder = productRepo.getProductById(productId);
            if (productToOrder.isEmpty()) {
                throw new IllegalArgumentException("Product not found");
            }
            products.add(new Product(productToOrder.get().id(), productToOrder.get().name(), new Stock(productToOrder.get().stock().unitType, new BigDecimal(1))));
        }

        Order newOrder = new Order(id, products);

        return orderRepo.addOrder(newOrder);
    }

    public Order addOrder(List<String> productIds) {
        return addOrder(UUID.randomUUID().toString(), productIds);
    }

    public List<Order> getAllOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderRepo.getOrders();
        List<Order> returnValue = new ArrayList<>();
        for (Order order : orders) {
            if(order.status() == status) {
                returnValue.add(order);
            }
        }
        return returnValue;
    }

    public void updateOrderStatus(String orderId, OrderStatus status) {
        orderRepo.addOrder(orderRepo.getOrderById(orderId).withStatus(status));
        if(status == OrderStatus.CANCELED) {
            orderRepo.getOrderById(orderId).products().forEach(product -> {
                productRepo.getProductById(product.id()).ifPresent(p -> p.stock().increaseAmount(product.stock().getAmount()));
            });
        }
        if(status == OrderStatus.PLACED) {
            orderRepo.getOrderById(orderId).products().forEach(product -> {
                productRepo.getProductById(product.id()).ifPresent(p -> {p.stock().reduceAmount(product.stock().getAmount());});
            });
        }
    }

    public Map<OrderStatus, Order> getOldestOrderPerStatus()
    {
        Map <OrderStatus, Order> orders = new HashMap<>();
        Arrays.stream(OrderStatus.values()).forEach(status -> {
            Optional<Order> o = getAllOrdersByStatus(status).stream().min(
                    Comparator.comparing(Order::orderTime)
            );
            o.ifPresent(order -> orders.put(status, order));
        });

        return orders;
    }

    public void updateOrderStatus(String orderId, String status) {
        updateOrderStatus(orderId, OrderStatus.valueOf(status));
    }

    public void printStock(){
        productRepo.printStock();
    }
}
