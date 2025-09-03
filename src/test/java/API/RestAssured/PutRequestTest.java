package API.RestAssured;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Feature;

public class PutRequestTest {

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("User data storage")
    @DisplayName("Тестирование тестового запроса Put c обновлением данных Users по полю job")
    public void putRequestCheckStatusCodeAndJsonBody() {
        // 1. Создаем пользователя и получаем его ID
        UserDTO createUser = new UserDTO(TestData.NAME, TestData.INITIAL_JOB);

        Response createResponse = RestAssured.given()
                .header("x-api-key", TestData.API_KEY)
                .spec(Specifications.requestSpecification())
                .body(createUser)
                .post(TestData.USERS_ENDPOINT);

        String userId = createResponse.then()
                .statusCode(201)
                .body("name", Matchers.equalTo(TestData.NAME))
                .body("job", Matchers.equalTo(TestData.INITIAL_JOB))
                .extract()
                .path("id");

        // 2. Обновляем данные пользователя
        UserDTO updateUser = new UserDTO(TestData.NAME, TestData.UPDATED_JOB);

        RestAssured.given()
                .header("x-api-key", TestData.API_KEY)
                .spec(Specifications.requestSpecification())
                .body(updateUser)
                .put(TestData.USERS_ENDPOINT + "/" + userId)
                .then()
                .statusCode(200)
                .body("name", Matchers.equalTo(TestData.NAME))
                .body("job", Matchers.equalTo(TestData.UPDATED_JOB));
    }

}