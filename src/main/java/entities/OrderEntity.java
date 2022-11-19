package entities;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import pojos.CreateOrderRequest;

import static io.restassured.RestAssured.given;

public class OrderEntity extends RestEntity{

    //get ingredients
    @Step("get all available ingredients via /api/ingredients")
    public Response getIngredients() {
        return given()
                .spec(getDefaultRequestSpec())
                .get("/api/ingredients");
    }

    //create
    @Step("create a new order for authorized user via /api/orders")
    public ValidatableResponse create(CreateOrderRequest createOrderRequest, String token) {
        return given()
                .header("Authorization", token)
                .spec(getDefaultRequestSpec())
                .body(createOrderRequest)
                .post( "/api/orders")
                .then();
    }

    @Step("create a new order for unauthorized user via /api/orders")
    public ValidatableResponse create(CreateOrderRequest createOrderRequest) {
        return given()
                .spec(getDefaultRequestSpec())
                .body(createOrderRequest)
                .post( "/api/orders")
                .then();
    }


    //get orders
    @Step("get orders for unauthorized user via /api/orders")
    public  ValidatableResponse getOrders() {
        return given()
                .spec(getDefaultRequestSpec())
                .get("/api/orders")
                .then();
    }

    @Step("get orders for authorized user via /api/orders")
    public ValidatableResponse getOrders(String token) {
        return given()
                .header("Authorization", token)
                .spec(getDefaultRequestSpec())
                .get("/api/orders")
                .then();
    }

}
