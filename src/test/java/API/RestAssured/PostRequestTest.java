package API.RestAssured;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Feature;

public class PostRequestTest {

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("API")
    @DisplayName("Тестирование тестового запроса Post с проверкой status code = 201")
    public void postRequestCheckStatusCode() {
        UserDTO user = new UserDTO(TestData.NAME, TestData.INITIAL_JOB);

        RestAssured.given()
                .header("x-api-key", TestData.API_KEY)
                .spec(Specifications.requestSpecification())
                .body(user)
                .post(TestData.USERS_ENDPOINT)
                .then()
                .statusCode(201);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("API")
    @DisplayName("Тестирование тестового запроса Post c проверкой key/value по полям name, job")
    public void postRequestCheckResponseJsonBody() {
        UserDTO user = new UserDTO(TestData.NAME, TestData.INITIAL_JOB);

        ValidatableResponse response = RestAssured.given()
                .header("x-api-key", TestData.API_KEY)
                .spec(Specifications.requestSpecification())
                .body(user)
                .post(TestData.USERS_ENDPOINT)
                .then()
                .statusCode(201);

        response.assertThat()
                .body("name", Matchers.equalTo(TestData.NAME))
                .body("job", Matchers.equalTo(TestData.INITIAL_JOB));
    }

}