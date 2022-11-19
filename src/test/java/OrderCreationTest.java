import configs.Config;
import entities.OrderEntity;
import entities.UserEntity;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojos.CreateOrderRequest;
import pojos.CreateUserRequest;
import pojos.DeleteUserRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static generators.UserRequestGenerator.getRandomUserRequest;
import static generators.OrderRequestGenerator.getRandomOrderRequest;
import static helpers.RandomList.pickNRandom;
import static org.hamcrest.CoreMatchers.equalTo;

public class OrderCreationTest {

    private OrderEntity randomOrder;
    private UserEntity randomUser;
    private String token;

    @Before
    public void setUp() throws InterruptedException {
        randomUser = new UserEntity();
        randomOrder = new OrderEntity();
        //adding delay because I'm getting "too many requests" in allure
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @DisplayName("создание заказа с авторизацией и ингредиентами")
    public void authorizedOrderCreationWithIngredientsTest() {
        CreateUserRequest randomUserRequest = getRandomUserRequest();
        token = randomUser.create(randomUserRequest)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("accessToken", Matchers.notNullValue())
                .and()
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");

        List<Map<String, String>> ingredients = randomOrder.getIngredients().jsonPath().getList("data");
        List<Map<String, String>> randomIngredients = pickNRandom(ingredients, 3);
        List<String> hashCodes = randomIngredients.stream().map(ingredient -> ingredient.get("_id")).collect(Collectors.toList());

        CreateOrderRequest orderRequest = getRandomOrderRequest(hashCodes);
        randomOrder.create(orderRequest, token)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("order.owner.email", equalTo(randomUserRequest.getEmail().toLowerCase()))
                .and()
                .body("order.ingredients._id", Matchers.contains(hashCodes.toArray()));
    }

    @Test
    @DisplayName("создание заказа с авторизацией и без ингредиентов")
    public void authorizedOrderCreationWithoutIngredientsTest() {
        CreateUserRequest randomUserRequest = getRandomUserRequest();
        token = randomUser.create(randomUserRequest)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("accessToken", Matchers.notNullValue())
                .and()
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");

        CreateOrderRequest orderRequest = getRandomOrderRequest(null);
        randomOrder.create(orderRequest, token)
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    //bug or feature? mixed right and wrong hash codes don't fail
    @Test
    @DisplayName("создание заказа с авторизацией и неверным хешем")
    @Issue("potential bug")
    public void authorizedOrderCreationWithWrongHashTest() {
        CreateUserRequest randomUserRequest = getRandomUserRequest();
        token = randomUser.create(randomUserRequest)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("accessToken", Matchers.notNullValue())
                .and()
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");

        List<Map<String, String>> ingredients = randomOrder.getIngredients().jsonPath().getList("data");
        List<Map<String, String>> randomIngredients = pickNRandom(ingredients, 1);
        List<String> hashCodes = randomIngredients.stream().map(ingredient -> ingredient.get("_id")).collect(Collectors.toList());
        String wrongHash = hashCodes.toString().substring(1, Config.getHashCodeLength() - 1) + RandomStringUtils.randomNumeric(2);
        hashCodes.set(0, wrongHash);
        CreateOrderRequest orderRequest = getRandomOrderRequest(hashCodes);

        randomOrder.create(orderRequest, token)
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("One or more ids provided are incorrect"));
    }

    @Test
    @DisplayName("создание заказа без авторизации")
    public void notAuthorizedOrderCreationTest() {
        List<Map<String, String>> ingredients = randomOrder.getIngredients().jsonPath().getList("data");
        List<Map<String, String>> randomIngredients = pickNRandom(ingredients, 3);
        List<String> hashCodes = randomIngredients.stream().map(ingredient -> ingredient.get("_id")).collect(Collectors.toList());

        CreateOrderRequest orderRequest = getRandomOrderRequest(hashCodes);

        randomOrder.create(orderRequest)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order.number", Matchers.notNullValue());
    }


    @After
    public void tearDown() {
        if (token != null) {
            DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
            deleteUserRequest.setToken(token);

            randomUser.delete(deleteUserRequest)
                    .assertThat()
                    .statusCode(HttpStatus.SC_ACCEPTED)
                    .and()
                    .body("success", equalTo(true));
        }

    }

}
