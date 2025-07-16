import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ShopServiceTest {

    @Test
    void addOrderTest() {
        //GIVEN
        Product[] productData = {
                new Product("1", "Apfel", new Stock(Stock.UnitType.PIECE, new BigDecimal(20)))};
        List<Product> products = new ArrayList<>(List.of(productData));
        ShopService shopService = new ShopService(new ProductRepo(products), new OrderMapRepo());
        List<String> productsIds = List.of("1");

        //WHEN
        Order actual = shopService.addOrder(productsIds);

        //THEN
        Order expected = new Order("-1", List.of(new Product("1", "Apfel", new Stock(Stock.UnitType.PIECE, new BigDecimal(20)))));
        assertEquals(expected.products(), actual.products());
        assertNotNull(expected.id());
    }

    @Test
    void addOrderTest_whenInvalidProductId_expectNull() {
        //GIVEN
        Product[] productData = {
                new Product("1", "Apfel", new Stock(Stock.UnitType.PIECE, new BigDecimal(20)))};
        List<Product> products = new ArrayList<>(List.of(productData));
        ShopService shopService = new ShopService(new ProductRepo(products), new OrderMapRepo());
        List<String> productsIds = List.of("1", "2");

        try {
            shopService.addOrder(productsIds);
            fail();
        }catch (IllegalArgumentException e) {
            assert(true);
        }

    }

    @Test
    void changeStatusTest() {
        Product[] productData = {
                new Product("1", "Apfel", new Stock(Stock.UnitType.PIECE, new BigDecimal(20)))};
        List<Product> products = new ArrayList<>(List.of(productData));
        ShopService shopService = new ShopService(new ProductRepo(products), new OrderMapRepo());
        List<String> productsIds = List.of("1");
        Order o = shopService.addOrder(productsIds);
        shopService.updateOrderStatus(o.id(), OrderStatus.PLACED);

        assert shopService.getAllOrdersByStatus(OrderStatus.PLACED).size() == 1;
        assert shopService.getAllOrdersByStatus(OrderStatus.OPEN).isEmpty();
    }

    @Test
    void getOldestOrderPerStatusTest() {

        Product[] productData = {
                new Product("0", "Apfel", new Stock(Stock.UnitType.PIECE, new BigDecimal(20))),
                new Product("1", "Banane", new Stock(Stock.UnitType.PIECE, new BigDecimal(20))),
                new Product("2", "Kiwi", new Stock(Stock.UnitType.PIECE, new BigDecimal(20)))};
        List<Product> products = new ArrayList<>(List.of(productData));

        ShopService shopService = new ShopService(new ProductRepo(products), new OrderMapRepo());

        shopService.addOrder(Arrays.asList("0", "0", "0"));
        shopService.addOrder(Arrays.asList("2", "0", "1"));
        shopService.addOrder(Arrays.asList("2", "2", "2"));
        shopService.addOrder(Arrays.asList("2", "0", "1"));
        shopService.addOrder(Arrays.asList("2", "2", "2"));
        shopService.addOrder(Arrays.asList("0", "0", "0"));
        shopService.addOrder(Arrays.asList("2", "0", "1"));
        shopService.addOrder(Arrays.asList("2", "2", "2"));
        shopService.addOrder(Arrays.asList("2", "0", "1"));


        List<Order> orders = shopService.getAllOrdersByStatus(OrderStatus.OPEN);
        shopService.updateOrderStatus(orders.get(0).id(), OrderStatus.PLACED);
        shopService.updateOrderStatus(orders.get(1).id(), OrderStatus.PLACED);
        shopService.updateOrderStatus(orders.get(2).id(), OrderStatus.DONE);
        shopService.updateOrderStatus(orders.get(3).id(), OrderStatus.DONE);
        shopService.updateOrderStatus(orders.get(4).id(), OrderStatus.CANCELED);
        shopService.updateOrderStatus(orders.get(5).id(), OrderStatus.DONE);
        shopService.updateOrderStatus(orders.get(6).id(), OrderStatus.SHIPPING);
        shopService.updateOrderStatus(orders.get(7).id(), OrderStatus.SHIPPING);

        Map<OrderStatus, Order> oldestOrders = shopService.getOldestOrderPerStatus();

        List<Order> placedOrders = shopService.getAllOrdersByStatus(OrderStatus.PLACED);
        List<Order> doneOrders = shopService.getAllOrdersByStatus(OrderStatus.DONE);
        List<Order> cancelledOrders = shopService.getAllOrdersByStatus(OrderStatus.CANCELED);
        List<Order> shippingOrders = shopService.getAllOrdersByStatus(OrderStatus.SHIPPING);

        Collections.sort(placedOrders, (o1, o2) -> o1.orderTime().isBefore(o2.orderTime()) ? -1 : 1);
        Collections.sort(doneOrders, (o1, o2) -> o1.orderTime().isBefore(o2.orderTime()) ? -1 : 1);
        Collections.sort(cancelledOrders, (o1, o2) -> o1.orderTime().isBefore(o2.orderTime()) ? -1 : 1);
        Collections.sort(shippingOrders, (o1, o2) -> o1.orderTime().isBefore(o2.orderTime()) ? -1 : 1);


        assertEquals(oldestOrders.get(OrderStatus.OPEN).id(), orders.get(8).id());
        assertEquals(oldestOrders.get(OrderStatus.PLACED).id(), placedOrders.get(0).id());
        assertEquals(oldestOrders.get(OrderStatus.DONE).id(), doneOrders.get(0).id());
        assertEquals(oldestOrders.get(OrderStatus.CANCELED).id(), cancelledOrders.get(0).id());
        assertEquals(oldestOrders.get(OrderStatus.SHIPPING).id(), shippingOrders.get(0).id());


    }
}
