package generators;

import pojos.AuthorizeUserRequest;
import pojos.CreateUserRequest;

public class AuthorizeRequestGenerator {
    public static AuthorizeUserRequest from(CreateUserRequest createUserRequest) {
        AuthorizeUserRequest authorizeUserRequest = new AuthorizeUserRequest();
        authorizeUserRequest.setEmail(createUserRequest.getEmail());
        authorizeUserRequest.setPassword(createUserRequest.getPassword());
        return authorizeUserRequest;
    }
}
