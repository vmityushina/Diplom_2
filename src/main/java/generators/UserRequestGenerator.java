package generators;

import org.apache.commons.lang3.RandomStringUtils;
import pojos.CreateUserRequest;

public class UserRequestGenerator {
    public static CreateUserRequest getRandomUserRequest() {
        CreateUserRequest userRequest = new CreateUserRequest(RandomStringUtils.randomAlphabetic(6) + "@yandex.ru", "password", "fistname");
        return userRequest;
    }
}
