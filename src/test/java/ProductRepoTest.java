import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepoTest {

    @org.junit.jupiter.api.Test
    void getProducts() {
        //GIVEN
        ProductRepo repo = new ProductRepo();

        //WHEN
        List<Product> actual = repo.getProducts();

        //THEN
        List<Product> expected = new ArrayList<>();
        expected.add(new Product("1", "Apfel", new Stock(Stock.UnitType.PIECE, new BigDecimal(20))));
        assertEquals(actual, expected);
    }

    @org.junit.jupiter.api.Test
    void getProductById() {
        //GIVEN
        ProductRepo repo = new ProductRepo();

        //WHEN
        Optional<Product> actual = repo.getProductById("1");

        //THEN
        Product expected = new Product("1", "Apfel", new Stock(Stock.UnitType.PIECE, new BigDecimal(20)));
        assertEquals(actual, Optional.of(expected));
    }

    @org.junit.jupiter.api.Test
    void addProduct() {
        //GIVEN
        ProductRepo repo = new ProductRepo();
        Product newProduct = new Product("2", "Banane", new Stock(Stock.UnitType.PIECE, new BigDecimal(20)));

        //WHEN
        Product actual = repo.addProduct(newProduct);

        //THEN
        Product expected = new Product("2", "Banane", new Stock(Stock.UnitType.PIECE, new BigDecimal(20)));
        assertEquals(actual, expected);
        assertEquals(repo.getProductById("2"), Optional.of(expected));
    }

    @org.junit.jupiter.api.Test
    void removeProduct() {
        //GIVEN
        ProductRepo repo = new ProductRepo();

        //WHEN
        repo.removeProduct("1");

        //THEN
        assert(repo.getProductById("1").isEmpty());
    }
}
