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

public class GetOrderTests {

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
    @DisplayName("Получение заказов авторизованным пользователем")
    @Description("Сервер вернёт 50 последних заказов пользователя")
    public void checkUserOrderGetting() {
        ingredients.add(ingredient_1);
        ingredients.add(ingredient_2);
        ValidatableResponse ordersResponse = orders.createOrderResponse(new Order(ingredients), token);

        String orderId = ordersResponse.extract().jsonPath().getString("order._id");
        int orderNumber = ordersResponse.extract().jsonPath().getInt("order.number");

        ValidatableResponse getOrdersResponse = orders.getOrdersResponse(token);

        orders.checkCreatedOrder(getOrdersResponse, orderId, orderNumber);
    }

    @Test
    @DisplayName("Получение заказов неавторизованным пользователем")
    @Description("Если выполнить запрос без авторизации, вернётся код ответа 401 Unauthorized")
    public void checkUserOrderUnauthorized() {
        ValidatableResponse getOrdersResponse = orders.getOrdersUnauthorized();

        orders.getOrderUnauthorized(getOrdersResponse);
    }

    @After
    public void deleteUser() {
        if (token != null) {
            users.deleteUser(token);
        }
    }
}
