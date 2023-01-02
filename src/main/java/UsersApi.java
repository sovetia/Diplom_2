import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UsersApi {

    @Step("Get user. Send GET request to /api/auth/user")
    public ValidatableResponse getUser(String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .and()
                .when()
                .get("/api/auth/user")
                .then();
    }

    @Step("Get user unauthorized. Send GET request to /api/auth/user")
    public ValidatableResponse getUserUnauthorized() {
        return given()
                .contentType(ContentType.JSON)
                .and()
                .when()
                .get("/api/auth/user")
                .then();
    }

    @Step("Edit user unauthorized. Send PATCH request to /api/auth/user")
    public ValidatableResponse editUserUnauthorized(Object body) {
        return given()
                .contentType(ContentType.JSON)
                .and()
                .body(body)
                .when()
                .patch("/api/auth/user")
                .then();
    }

    @Step("Edit user. Send PATCH request to /api/auth/user")
    public ValidatableResponse editUserResponse(Object body, String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .and()
                .body(body)
                .when()
                .patch("/api/auth/user")
                .then();
    }

    @Step("Login user. Send POST request to /api/auth/login")
    public ValidatableResponse loginUserResponse(Object body) {
        return given()
                .contentType(ContentType.JSON)
                .and()
                .body(body)
                .when()
                .post("/api/auth/login")
                .then();
    }

    @Step("Create user. Send POST request to /api/auth/register")
    public ValidatableResponse registerUserResponse(Object body) {
        return given()
                .contentType(ContentType.JSON)
                .and()
                .body(body)
                .when()
                .post("/api/auth/register")
                .then();
    }

    @Step("Delete user. Send DELETE request to /api/auth/user")
    public void deleteUser(String accessToken) {
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .when()
                .delete("/api/auth/user")
                .then()
                .assertThat().statusCode(202);
    }

    @Step("Check email already exists")
    public void checkEmailAlreadyExists(ValidatableResponse response) {
        response
                .assertThat().statusCode(403)
                .assertThat().body("success", equalTo(false)).and()
                .assertThat().body("message", equalTo("User with such email already exists"));
    }

    @Step("Check user edited successfully")
    public void checkUserEditedSuccess(ValidatableResponse response, String email, String name) {
        response
                .assertThat().statusCode(200)
                .assertThat().body("success", equalTo(true)).and()
                .assertThat().body("user.email", equalTo(email.toLowerCase())).and()
                .assertThat().body("user.name", equalTo(name));
    }

    @Step("Check get user unauthorized")
    public void checkUserUnauthorized(ValidatableResponse response) {
        response
                .assertThat().statusCode(401)
                .assertThat().body("success", equalTo(false)).and()
                .assertThat().body("message", equalTo("You should be authorised"));
    }

    @Step("Check get user")
    public void checkGetUserSuccess(ValidatableResponse response, String email, String name) {
        response
                .assertThat().statusCode(200)
                .assertThat().body("success", equalTo(true)).and()
                .assertThat().body("user.email", equalTo(email.toLowerCase())).and()
                .assertThat().body("user.name", equalTo(name));
    }

    @Step("Check email or password are incorrect on login")
    public void checkUserLoginEmailOrPwIncorrect(ValidatableResponse response) {
        response
                .assertThat().statusCode(401)
                .assertThat().body("success", equalTo(false)).and()
                .assertThat().body("message", equalTo("email or password are incorrect"));
    }

    @Step("Check user logged in successfully")
    public void checkUserLoginSuccess(ValidatableResponse response, String email, String name) {
        response
                .assertThat().statusCode(200)
                .assertThat().body("success", equalTo(true)).and()
                .assertThat().body("user.email", equalTo(email.toLowerCase())).and()
                .assertThat().body("user.name", equalTo(name)).and()
                .assertThat().body("accessToken", notNullValue());
    }

    @Step("Check user registered successfully")
    public void checkUserRegisteredSuccess(ValidatableResponse registerUserResponse, String email, String name) {
        registerUserResponse
                .assertThat().statusCode(200)
                .assertThat().body("success", equalTo(true)).and()
                .assertThat().body("user.email", equalTo(email.toLowerCase())).and()
                .assertThat().body("user.name", equalTo(name)).and()
                .assertThat().body("accessToken", notNullValue());

    }

    @Step("Check user registered already exists")
    public void checkUserRegisteredExists(ValidatableResponse response) {
        response.
                assertThat().statusCode(403).and()
                .assertThat().body("success", equalTo(false)).and()
                .assertThat().body("message", equalTo("User already exists"));

    }

    @Step("Check user registered has no email or password")
    public void checkUserRegisteredHasNoEmailOrPw(ValidatableResponse response) {
        response
                .assertThat().statusCode(403)
                .assertThat().body("success", equalTo(false)).and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"));
    }
}