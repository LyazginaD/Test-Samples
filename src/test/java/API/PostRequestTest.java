package API;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Feature;

public class PostRequestTest {
    private static final String API_KEY = "reqres-free-v1";
    private static final String USERS_ENDPOINT = "/api/users";
    private static final String NAME = "morpheus";
    private static final String JOB = "leader";

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("API")
    @DisplayName("Тестирование тестового запроса Post с проверкой status code = 201")
    public void postRequestCheckStatusCode() {
        UserDTO user = new UserDTO(NAME, JOB);

        RestAssured.given()
                .header("x-api-key", API_KEY)
                .spec(Specifications.requestSpecification())
                .body(user)
                .post(USERS_ENDPOINT)
                .then()
                .statusCode(201);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("API")
    @DisplayName("Тестирование тестового запроса Post c проверкой key/value по полям name, job")
    public void postRequestCheckResponseJsonBody() {
        UserDTO user = new UserDTO(NAME, JOB);

        ValidatableResponse response = RestAssured.given()
                .header("x-api-key", API_KEY)
                .spec(Specifications.requestSpecification())
                .body(user)
                .post(USERS_ENDPOINT)
                .then()
                .statusCode(201);

        response.assertThat()
                .body("name", Matchers.equalTo(NAME))
                .body("job", Matchers.equalTo(JOB));
    }
}