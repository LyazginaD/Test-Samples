package API;

import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Feature;

public class GetRequestTest {
    private static final String API_KEY = "reqres-free-v1";
    private static final String USER_ENDPOINT = "/api/users/2";
    private static final String SCHEMA_PATH = "schemaUsersById.json";

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("API")
    @DisplayName("Тестирование запроса Get c проверкой status code = 200")
    public void getRequestCheckStatusCode() {
        RestAssured.given()
                .header("x-api-key", API_KEY)
                .spec(Specifications.requestSpecification())
                .get(USER_ENDPOINT)
                .then()
                .statusCode(200);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("API")
    @DisplayName("Тестирование запроса Get c проверкой key/value по полям id, email, first_name, last_name")
    public void getRequestCheckResponseJsonBody() {
        ValidatableResponse response = RestAssured.given()
                .header("x-api-key", API_KEY)
                .spec(Specifications.requestSpecification())
                .get(USER_ENDPOINT)
                .then()
                .statusCode(200);

        response.assertThat()
                .body("data.id", Matchers.equalTo(2))
                .body("data.email", Matchers.equalTo("janet.weaver@reqres.in"))
                .body("data.first_name", Matchers.equalTo("Janet"))
                .body("data.last_name", Matchers.equalTo("Weaver"));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("API")
    @DisplayName("Тестирование запроса Get c валидацией ответа по json схеме")
    public void getRequestCheckResponseWithJsonSchema() {
        RestAssured.given()
                .header("x-api-key", API_KEY)
                .spec(Specifications.requestSpecification())
                .get(USER_ENDPOINT)
                .then()
                .spec(Specifications.responseSpecificationScOk())
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/schemaUsersById.json"));
    }
}

