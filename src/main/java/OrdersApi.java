import pojo.Order;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrdersApi {

    @Step("Get orders. Send GET request to /api/orders")
    public ValidatableResponse getOrdersResponse(String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .and()
                .when()
                .get("/api/orders")
                .then();
    }

    @Step("Get orders unauthorized. Send GET request to /api/orders")
    public ValidatableResponse getOrdersUnauthorized() {
        return given()
                .contentType(ContentType.JSON)
                .and()
                .when()
                .get("/api/orders")
                .then();
    }

    @Step("Get ingredients. Send GET request to /api/ingredients")
    public ValidatableResponse getIngredientsResponse() {
        return given()
                .contentType(ContentType.JSON)
                .and()
                .when()
                .get("/api/ingredients")
                .then();
    }

    @Step("Create order. Send POST request to /api/orders")
    public ValidatableResponse createOrderResponse(Order ingredients, String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .and()
                .body(ingredients)
                .when()
                .post("/api/orders")
                .then();
    }

    @Step("Create order unauthorized. Send POST request to /api/orders")
    public ValidatableResponse createOrderUnauthorized(Order ingredients) {
        return given()
                .contentType(ContentType.JSON)
                .and()
                .body(ingredients)
                .when()
                .post("/api/orders")
                .then();
    }

    @Step("Check get order unauthorized")
    public void getOrderUnauthorized(ValidatableResponse response) {
        response
                .assertThat().statusCode(401).and()
                .assertThat().body("success", equalTo(false)).and()
                .assertThat().body("message", equalTo("You should be authorised"));

    }

    @Step("Check created order")
    public void checkCreatedOrder(ValidatableResponse response, String orderId, int orderNumber) {
        response
                .assertThat().statusCode(200).and()
                .assertThat().body("success", equalTo(true)).and()
                .assertThat().body("orders[0]._id", equalTo(orderId)).and()
                .assertThat().body("orders[0].number", equalTo(orderNumber)).and()
                .assertThat().body("orders[0].ingredients", notNullValue());
    }

    @Step("Check order not created with incorrect hash ingredients")
    public void checkOrderNotCreatedIncorrectHash(ValidatableResponse response) {
        response.assertThat().statusCode(500);
    }

    @Step("Check order not created without ingredients")
    public void checkOrderNotCreatedWithoutIngredients(ValidatableResponse response) {
        response
                .assertThat().statusCode(400).and()
                .assertThat().body("success", equalTo(false)).and()
                .assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("Check order created successfully")
    public void checkOrderCreatedSuccess(ValidatableResponse response) {
        response
                .assertThat().statusCode(200).and()
                .assertThat().body("success", equalTo(true)).and()
                .assertThat().body("order.number", notNullValue());
    }

}