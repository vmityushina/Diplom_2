import entities.UserEntity;
import generators.AuthorizeRequestGenerator;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojos.AuthorizeUserRequest;
import pojos.CreateUserRequest;
import pojos.DeleteUserRequest;

import java.util.concurrent.TimeUnit;

import static generators.UserRequestGenerator.getRandomUserRequest;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserLoginTest {

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
        //adding delay because I'm getting "too many requests" in allure
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @DisplayName("логин под существующим пользователем")
    public void successfulUserLoginTest() {
        AuthorizeUserRequest authorizeUserRequest = AuthorizeRequestGenerator.from(randomUserRequest);
        randomUser.authorize(authorizeUserRequest, token)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("accessToken", Matchers.notNullValue())
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("логин с неверным паролем")
    public void wrongPasswordLoginTest() {
        AuthorizeUserRequest authorizeUserRequest = AuthorizeRequestGenerator.from(randomUserRequest);
        authorizeUserRequest.setPassword(RandomStringUtils.randomAlphabetic(10));
        randomUser.authorize(authorizeUserRequest, token)
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
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
