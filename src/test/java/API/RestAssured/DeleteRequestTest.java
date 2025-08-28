package API.RestAssured;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Feature;

public class DeleteRequestTest {
    private static final String API_KEY = "reqres-free-v1";
    private static final String USERS_ENDPOINT = "/api/users";

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("API")
    @DisplayName("Тестирование запроса Delete c удалением пользователя")
    public void deleteRequestCheckStatusCode() {
        // Создаем пользователя и получаем его ID
        UserDTO user = new UserDTO("morpheus", "leader");

        Response createResponse = RestAssured.given()
                .header("x-api-key", API_KEY)
                .spec(Specifications.requestSpecification())
                .body(user)
                .post(USERS_ENDPOINT);

        createResponse.then()
                .statusCode(201);

        String userId = createResponse.body()
                .jsonPath()
                .getString("id");

        // Удаляем созданного пользователя и проверяем статус код
        RestAssured.given()
                .header("x-api-key", API_KEY)
                .spec(Specifications.requestSpecification())
                .delete(USERS_ENDPOINT + "/" + userId)
                .then()
                .statusCode(204);
    }
}