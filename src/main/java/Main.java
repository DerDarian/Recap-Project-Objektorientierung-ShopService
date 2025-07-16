import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {

        Product[] productData = {
                new Product("0", "Apfel", new Stock(Stock.UnitType.PIECE, new BigDecimal(20))),
                new Product("1", "Banane", new Stock(Stock.UnitType.PIECE, new BigDecimal(20))),
                new Product("2", "Kiwi", new Stock(Stock.UnitType.PIECE, new BigDecimal(20))),
                new Product("3", "Birne", new Stock(Stock.UnitType.PIECE, new BigDecimal(20))),
                new Product("4", "Mango", new Stock(Stock.UnitType.PIECE, new BigDecimal(20)))};

        List<Product> products = new ArrayList<>(List.of(productData));

        ShopService shopService = new ShopService(new ProductRepo(products), new OrderMapRepo());

        shopService.addOrder(Arrays.asList("0", "0", "0"));
        shopService.addOrder(Arrays.asList("2", "0", "1"));
        shopService.addOrder(Arrays.asList("2", "2", "2"));

        readTransactionFile(shopService);
        shopService.printStock();
    }

    private static void branchCommand(ShopService shopService, String command, String[] params){
        switch (command) {
            case "addOrder":
                if(params.length < 2){
                    throw new IllegalArgumentException("adding an Order requires at least two parameters: Order Id and any number (1+) of product IDs");
                }
                shopService.addOrder(params[0], Arrays.asList(Arrays.copyOfRange(params, 1, params.length)));
                break;
            case "setStatus":
                if(params.length != 2){
                    throw new IllegalArgumentException("setStatus needs exactly 2 parameters: Order ID and Status");
                }
                shopService.updateOrderStatus(params[0], params[1]);
                break;
            case "printOrders":
                Arrays.stream(OrderStatus.values()).forEach(status -> {
                    shopService.getAllOrdersByStatus(status).forEach(System.out::println);
                });
                break;
            case "printStock":
                shopService.printStock();
        }
    }

    private static void readTransactionFile(ShopService shopService) throws IOException {
        try( Stream<String> lines = Files.lines(Paths.get("src/main/transactions.txt"))){
            lines.forEach(line -> {
                System.out.println("Executing line: " + line);
                String[] commandLine = line.split(" ");
                String[] params = {};
                if(commandLine.length > 1){
                    params = Arrays.copyOfRange(commandLine, 1, commandLine.length);
                }
                branchCommand(shopService, commandLine[0], params);
            });
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
