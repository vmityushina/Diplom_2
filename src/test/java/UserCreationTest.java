import entities.UserEntity;
import io.qameta.allure.junit4.DisplayName;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojos.CreateUserRequest;
import pojos.DeleteUserRequest;

import java.util.concurrent.TimeUnit;

import static generators.UserRequestGenerator.getRandomUserRequest;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserCreationTest {
    private UserEntity randomUser;
    private String token;

    @Before
    public void setUp() throws InterruptedException {
        randomUser = new UserEntity();
        //adding delay because I'm getting "too many requests" in allure
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @DisplayName("создание уникального пользователя")
    public void uniqueUserCreationTest() {

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
    }

    @Test
    @DisplayName("создание пользователя, который уже зарегистрирован")
    public void duplicateUserCreationTest() {
        CreateUserRequest randomUserRequest = getRandomUserRequest();
        randomUser.create(randomUserRequest);
        randomUser.create(randomUserRequest)
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("создание пользователя с незаполненным емейлом")
    public void userCreationWithMissedEmailTest() {
        CreateUserRequest randomUserRequest = getRandomUserRequest();
        randomUserRequest.setEmail(null);
        randomUser.create(randomUserRequest)
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
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
