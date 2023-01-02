import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.User;

import static constants.Constants.*;

public class CreateUserTests {
    private String token;
    private UsersApi users = new UsersApi();

    @Before
    public void setUp() {
        RestAssured.baseURI = URL;
    }

    @Test
    @DisplayName("Создать уникального пользователя")
    @Description("Пользователя можно создать")
    public void checkUserCreation() {
        ValidatableResponse registerUserResponse = users.registerUserResponse(new User(USER_EMAIL, USER_PASSWORD, USER_NAME));

        token = registerUserResponse.extract().jsonPath().getString("accessToken");

        users.checkUserRegisteredSuccess(registerUserResponse, USER_EMAIL, USER_NAME);
    }

    @Test
    @DisplayName("Создать пользователя, который уже зарегистрирован")
    @Description("Если пользователь существует, вернётся код ответа 403 Forbidden")
    public void checkCanNotCreateTwoTheSameUsers() {
        ValidatableResponse firstUserResponse = users.registerUserResponse(new User(USER_EMAIL, USER_PASSWORD, USER_NAME));
        ValidatableResponse secondUserResponse = users.registerUserResponse(new User(USER_EMAIL, USER_PASSWORD, USER_NAME));

        users.checkUserRegisteredSuccess(firstUserResponse, USER_EMAIL, USER_NAME);

        token = firstUserResponse.extract().jsonPath().getString("accessToken");

        users.checkUserRegisteredExists(secondUserResponse);
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить одно из обязательных полей")
    @Description("Если нет одного из полей, вернётся код ответа 403 Forbidden.")
    public void checkUserNotCreatedWithoutMandatoryField() {
        ValidatableResponse registerUserResponse = users.registerUserResponse(new User(USER_EMAIL, "", USER_NAME));

        users.checkUserRegisteredHasNoEmailOrPw(registerUserResponse);
    }

    @After
    public void deleteUser() {
        if (token != null) {
            users.deleteUser(token);
        }
    }
}