package API.RestAssured;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import io.restassured.filter.log.LogDetail;
import java.util.concurrent.TimeUnit;

public class Specifications {

    private static final String BASE_URI = "https://reqres.in/";
    private static final long MAX_RESPONSE_TIME_SECONDS = 3L;

    public static RequestSpecification requestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setRelaxedHTTPSValidation()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification responseSpecificationScOk() {
        return new ResponseSpecBuilder()
                .log(LogDetail.STATUS)
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .expectResponseTime(Matchers.lessThanOrEqualTo(MAX_RESPONSE_TIME_SECONDS), TimeUnit.SECONDS)
                .build();
    }

}