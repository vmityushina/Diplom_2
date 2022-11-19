package generators;

import pojos.CreateOrderRequest;

import java.util.List;

public class OrderRequestGenerator {
    public static CreateOrderRequest getRandomOrderRequest(List<String> ingredients) {

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(ingredients);
        return createOrderRequest;
    }

}
