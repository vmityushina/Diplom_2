import entities.UserEntity;
import generators.UpdateUserRequestGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojos.CreateUserRequest;
import pojos.DeleteUserRequest;
import pojos.UpdateUserRequest;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static generators.UserRequestGenerator.getRandomUserRequest;
import static org.hamcrest.CoreMatchers.equalTo;

public class ChangeUserDataTest {
    private UserEntity randomUser;
    private String token;
    private CreateUserRequest randomUserRequest;

    @Before
    public void setUp() throws InterruptedException {
        randomUser = new UserEntity();
        randomUserRequest = getRandomUserRequest();
        token = randomUser.create(randomUserRequest)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("accessToken", Matchers.notNullValue())
                .and()
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");
        //adding delay because I'm gettin "too many requests" in allure
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @DisplayName("изменение пароля для авторизованного пользователя")
    public void changePasswordForAuthorizedUserTest() {
        UpdateUserRequest updateUserRequest = UpdateUserRequestGenerator.from(randomUserRequest);
        updateUserRequest.setPassword(RandomStringUtils.randomAlphabetic(10));
        randomUser.update(updateUserRequest, token)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("изменение емейла для авторизованного пользователя")
    public void changeEmailForAuthorizedUserTest() {
        UpdateUserRequest updateUserRequest = UpdateUserRequestGenerator.from(randomUserRequest);
        String newEmail = RandomStringUtils.randomAlphabetic(10);
        updateUserRequest.setEmail(newEmail);
        randomUser.update(updateUserRequest, token);

        Response response = randomUser.update(updateUserRequest, token);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true));
        Map<String, String> user = response.jsonPath().getMap("user");
        Assert.assertEquals(newEmail.toLowerCase(), user.get("email"));
    }

    @Test
    @DisplayName("изменение имени для авторизованного пользователя")
    public void changeNameForAuthorizedUserTest() {
        UpdateUserRequest updateUserRequest = UpdateUserRequestGenerator.from(randomUserRequest);
        String newName = RandomStringUtils.randomAlphabetic(10);
        updateUserRequest.setName(newName);

        Response response = randomUser.update(updateUserRequest, token);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", equalTo(true));
        Map<String, String> user = response.jsonPath().getMap("user");
        Assert.assertEquals(newName, user.get("name"));
    }

    @Test
    @DisplayName("изменение пароля для неавторизованного пользователя")
    public void changePasswordForNotAuthorizedUserTest() {
        UpdateUserRequest updateUserRequest = UpdateUserRequestGenerator.from(randomUserRequest);
        updateUserRequest.setPassword(RandomStringUtils.randomAlphabetic(10));
        randomUser.update(updateUserRequest, RandomStringUtils.randomAlphabetic(10))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
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
