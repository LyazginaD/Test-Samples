package JMeter;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.internal.shadowed.jackson.databind.JsonNode;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;
import static org.assertj.core.api.Assertions.assertThat;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

public class LoadTest {

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Feature("API")
    @DisplayName("Тестирование запроса Get от 10 пользователей")
    public void testPerformanceGet10() throws IOException {

        TestPlanStats stats = testPlan(
                threadGroup(5, 1,
                        httpSampler("https://reqres.in/api/users/2")
                                .header("x-api-key", "reqres-free-v1")),

                jtlWriter("E:/Prog/IdeaProjects/testing/allure-results")

        ).run();

        // Проверка времени выполнения
        assertThat(stats.overall().sampleTimePercentile99())
                .isLessThan(Duration.ofSeconds(5));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Feature("API")
    @DisplayName("Тестирование запроса Post от 5 пользователей")
    public void testPerformancePost5() throws IOException {

        String testName = "morpheus";
        String testJob = "leader";

        String[] lastResponse = new String[1];

        TestPlanStats stats = testPlan(
                threadGroup(5, 1,
                        httpSampler("https://reqres.in/api/users")
                                .post("{\"name\": \"morpheus\", \"job\": \"leader\"}", ContentType.APPLICATION_JSON)
                                .header("x-api-key", "reqres-free-v1")
                                .children(
                                        jsr223PostProcessor(s -> {
                                            lastResponse[0] = s.prev.getResponseDataAsString();
                                            s.log.info("Response: {}", lastResponse[0]);
                                            // Проверяем, что ответ получен
                                            assertThat(lastResponse[0])
                                                    .as("Ответ сервера не должен быть null")
                                                    .isNotNull();

                                            // Парсим JSON ответ
                                            JsonNode responseJson;
                                            try {
                                                ObjectMapper mapper = new ObjectMapper();
                                                responseJson = mapper.readTree(lastResponse[0]);
                                            } catch (Exception e) {
                                                throw new AssertionError("Не удалось распарсить JSON ответ: " + e.getMessage());
                                            }

                                            // Проверяем соответствие полей name и job
                                            assertThat(responseJson.get("name").asText())
                                                    .as("Поле name в ответе должно соответствовать отправленному")
                                                    .isEqualTo(testName);

                                            assertThat(responseJson.get("job").asText())
                                                    .as("Поле job в ответе должно соответствовать отправленному")
                                                    .isEqualTo(testJob);

                                            // Проверяем, что id > 0
                                            assertThat(responseJson.get("id").asInt())
                                                    .as("Поле id должно быть больше 0")
                                                    .isGreaterThan(0);

                                            // Проверяем, что createdAt не null
                                            assertThat(responseJson.get("createdAt").asText())
                                                    .as("Поле createdAt не должно быть null")
                                                    .isNotNull()
                                                    .isNotBlank();
                                        })
                                )
                ),
                jtlWriter("E:/Prog/IdeaProjects/testing/allure-results")
        ).run();

        // Проверка времени выполнения
        assertThat(stats.overall().sampleTimePercentile99())
                .isLessThan(Duration.ofSeconds(5));

    }

    @BeforeEach
    public void makeAPause(){
        try {
            Thread.sleep(5000); // Пауза 1 секунда
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}