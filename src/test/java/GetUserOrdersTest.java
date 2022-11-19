import entities.OrderEntity;
import entities.UserEntity;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
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

import static generators.OrderRequestGenerator.getRandomOrderRequest;
import static generators.UserRequestGenerator.getRandomUserRequest;
import static helpers.RandomList.pickNRandom;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetUserOrdersTest {

    private OrderEntity randomOrder;
    private UserEntity randomUser;
    private String token;

    @Before
    public void setUp() throws InterruptedException {
        randomUser = new UserEntity();
        randomOrder = new OrderEntity();
        //adding delay because I'm gettin "too many requests" in allure
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    @Description("Тест ручки /api/orders")
    public void unauthorizedGetOrdersTest() {
        randomOrder.getOrders()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Тест ручки /api/orders")
    public void authorizedGetOrdersTest() {

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


        randomOrder.getOrders(token)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("orders", Matchers.notNullValue());
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
