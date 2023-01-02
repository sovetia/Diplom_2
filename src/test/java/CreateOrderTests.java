import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.Order;
import pojo.User;

import java.util.ArrayList;
import java.util.List;

import static constants.Constants.*;

public class CreateOrderTests {
    public List<String> ingredients = new ArrayList<>();
    private String token;
    private String ingredient_1, ingredient_2;
    private UsersApi users = new UsersApi();
    private OrdersApi orders = new OrdersApi();

    @Before
    public void setUp() {
        RestAssured.baseURI = URL;
        ValidatableResponse registerUserResponse = users.registerUserResponse(new User(USER_EMAIL, USER_PASSWORD, USER_NAME));
        token = registerUserResponse.extract().jsonPath().getString("accessToken");

        ValidatableResponse ingredients = orders.getIngredientsResponse();
        ingredient_1 = ingredients.extract().jsonPath().getString("data[0]._id");
        ingredient_2 = ingredients.extract().jsonPath().getString("data[1]._id");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Заказ можно создать, передав ингредиенты")
    public void checkOrderCreation() {
        ingredients.add(ingredient_1);
        ingredients.add(ingredient_2);
        ValidatableResponse ordersResponse = orders.createOrderResponse(new Order(ingredients), token);

        orders.checkOrderCreatedSuccess(ordersResponse);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Заказ можно создать без регистрации, передав ингредиенты")
    public void checkSuccessOrderCreationUnauthorized() {
        ingredients.add(ingredient_1);
        ingredients.add(ingredient_2);
        ValidatableResponse ordersResponse = orders.createOrderUnauthorized(new Order(ingredients));

        orders.checkOrderCreatedSuccess(ordersResponse);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Если не передать ни один ингредиент, вернётся код ответа 400 Bad Request")
    public void checkOrderCreationWithoutIngredients() {
        ValidatableResponse ordersResponse = orders.createOrderResponse(new Order(ingredients), token);

        orders.checkOrderNotCreatedWithoutIngredients(ordersResponse);
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Если в запросе передан невалидный хеш ингредиента, вернётся код ответа 500 Internal Server Error")
    public void checkOrderCreationWithIncorrectHash() {
        ingredients.add("fake_hash_1");
        ingredients.add("fake_hash_2");
        ValidatableResponse ordersResponse = orders.createOrderResponse(new Order(ingredients), token);

        orders.checkOrderNotCreatedIncorrectHash(ordersResponse);
    }

    @After
    public void deleteUser() {
        if (token != null) {
            users.deleteUser(token);
        }
    }
}