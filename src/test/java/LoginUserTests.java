import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.User;

import static constants.Constants.*;

public class LoginUserTests {
    private String token;
    private UsersApi users = new UsersApi();

    @Before
    public void setUp() {
        RestAssured.baseURI = URL;

        ValidatableResponse registerUserResponse = users.registerUserResponse(new User(USER_EMAIL, USER_PASSWORD, USER_NAME));

        token = registerUserResponse.extract().jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Пользователь может залогиниться")
    public void checkUserAuthenticated() {
        ValidatableResponse loginUserResponse = users.loginUserResponse(new User(USER_EMAIL, USER_PASSWORD));

        users.checkUserLoginSuccess(loginUserResponse, USER_EMAIL, USER_NAME);
    }

    @Test
    @DisplayName("Логин с неверным логином и паролем")
    @Description("Если логин или пароль неверные или нет одного из полей, вернётся код ответа 401 Unauthorized")
    public void checkUserCanNotLoginWithIncorrectPassword() {
        ValidatableResponse loginUserResponse = users.loginUserResponse(new User(USER_EMAIL, "fake_password"));

        users.checkUserLoginEmailOrPwIncorrect(loginUserResponse);
    }

    @After
    public void deleteUser() {
        if (token != null) {
            users.deleteUser(token);
        }
    }
}