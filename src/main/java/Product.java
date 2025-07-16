import java.util.UUID;

public record Product(
        String id,
        String name,
        Stock stock
) {
    public Product(String name, Stock.UnitType unitType) {
        this(UUID.randomUUID().toString(), name, new Stock(unitType));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id.equals(product.id);
    }
}
