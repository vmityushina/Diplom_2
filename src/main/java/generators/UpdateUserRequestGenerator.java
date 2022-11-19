package generators;

import pojos.AuthorizeUserRequest;
import pojos.CreateUserRequest;
import pojos.UpdateUserRequest;

public class UpdateUserRequestGenerator {
    public static UpdateUserRequest from(CreateUserRequest createUserRequest) {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail(createUserRequest.getEmail());
        updateUserRequest.setPassword(createUserRequest.getPassword());
        updateUserRequest.setName(createUserRequest.getName());
        return updateUserRequest;
    }
}
