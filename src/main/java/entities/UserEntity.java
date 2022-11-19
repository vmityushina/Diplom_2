package entities;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import pojos.AuthorizeUserRequest;
import pojos.CreateUserRequest;
import pojos.DeleteUserRequest;
import pojos.UpdateUserRequest;

import static io.restassured.RestAssured.given;

public class UserEntity extends RestEntity{

    //create
    @Step("create a unique user via /api/auth/register")
    public ValidatableResponse create(CreateUserRequest createUserRequest) {
        return given()
                .spec(getDefaultRequestSpec())
                .body(createUserRequest)
                .post( "/api/auth/register")
                .then();
    }

    //delete
    @Step("delete a user via /api/auth/user")
    public ValidatableResponse delete(DeleteUserRequest deleteUserRequest) {
        return given()
                .header("Authorization", deleteUserRequest.getToken())
                .spec(getDefaultRequestSpec())
                .queryParam("email", deleteUserRequest.getEmail())
                .delete("/api/auth/user")
                .then();
    }

    //login
    @Step("authorize a user via /api/auth/login")
    public ValidatableResponse authorize(AuthorizeUserRequest authorizeUserRequest, String token) {
        return given()
                .header("Authorization", token)
                .spec(getDefaultRequestSpec())
                .body(authorizeUserRequest)
                .post("/api/auth/login")
                .then();
    }

    //change user data
    @Step("update user details via /api/auth/user")
    public Response update(UpdateUserRequest updateUserRequest, String token) {
        return given()
                .header("Authorization", token)
                .spec(getDefaultRequestSpec())
                .body(updateUserRequest)
                .patch("/api/auth/user");
    }
}
