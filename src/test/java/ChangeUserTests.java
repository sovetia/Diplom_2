import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.User;

import static constants.Constants.*;

public class ChangeUserTests {
    private String newEmail = "fake_user_email@yandex.ru";
    private String newPassword = "fake_user_password";
    private String newName = "fake_user_name";
    private String token;
    private UsersApi users = new UsersApi();

    @Before
    public void setUp() {
        RestAssured.baseURI = URL;
        ValidatableResponse registerUserResponse = users.registerUserResponse(new User(USER_EMAIL, USER_PASSWORD, USER_NAME));
        token = registerUserResponse.extract().jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    @Description("Если всё хорошо, сервер вернёт обновлённого пользователя")
    public void checkUserEditing() {
        ValidatableResponse editUserResponse = users.editUserResponse(new User(newEmail, newPassword, newName), token);

        users.checkUserEditedSuccess(editUserResponse, newEmail, newName);

        ValidatableResponse loginUserResponse = users.loginUserResponse(new User(newEmail, newPassword));
        users.checkUserLoginSuccess(loginUserResponse, newEmail, newName);
    }

    @Test
    @DisplayName("Изменение данных почты, которая уже используется")
    @Description("Если передать почту, которая уже используется, вернётся код ответа 403 Forbidden.")
    public void checkCanNotEditExistingEmail() {
        ValidatableResponse registerUserResponse = users.registerUserResponse(new User(newEmail, newPassword, newName));

        String newToken = registerUserResponse.extract().jsonPath().getString("accessToken");

        ValidatableResponse editSecondUserResponse = users.editUserResponse(new User(USER_EMAIL, USER_PASSWORD, USER_NAME), newToken);

        users.checkEmailAlreadyExists(editSecondUserResponse);

        users.deleteUser(newToken);
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("Если выполнить запрос без авторизации, вернётся код ответа 401 Unauthorized.")
    public void checkUserEditingUnauthorized() {
        ValidatableResponse editUserResponse = users.editUserUnauthorized(new User(newEmail, newPassword, newName));

        users.checkUserUnauthorized(editUserResponse);
    }

    @After
    public void deleteUser() {
        if (token != null) {
            users.deleteUser(token);
        }
    }
}