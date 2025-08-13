package API;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Feature;

public class PutRequestTest {
    private static final String API_KEY = "reqres-free-v1";
    private static final String USERS_ENDPOINT = "/api/users";
    private static final String NAME = "morpheus";
    private static final String INITIAL_JOB = "leader";
    private static final String UPDATED_JOB = "test_it";

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("API")
    @DisplayName("Тестирование тестового запроса Put c обновлением данных Users по полю job")
    public void putRequestCheckStatusCodeAndJsonBody() {
        // 1. Создаем пользователя и получаем его ID
        UserDTO createUser = new UserDTO(NAME, INITIAL_JOB);

        Response createResponse = RestAssured.given()
                .header("x-api-key", API_KEY)
                .spec(Specifications.requestSpecification())
                .body(createUser)
                .post(USERS_ENDPOINT);

        String userId = createResponse.then()
                .statusCode(201)
                .body("name", Matchers.equalTo(NAME))
                .body("job", Matchers.equalTo(INITIAL_JOB))
                .extract()
                .path("id");

        // 2. Обновляем данные пользователя
        UserDTO updateUser = new UserDTO(NAME, UPDATED_JOB);

        RestAssured.given()
                .header("x-api-key", API_KEY)
                .spec(Specifications.requestSpecification())
                .body(updateUser)
                .put(USERS_ENDPOINT + "/" + userId)
                .then()
                .statusCode(200)
                .body("name", Matchers.equalTo(NAME))
                .body("job", Matchers.equalTo(UPDATED_JOB));
    }
}