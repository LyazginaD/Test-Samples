package API.SQL;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class SQLTest {

    private static final String SUPABASE_URL = "https://vsopzyezklzzkkvkxgav.supabase.co/rest/v1";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZzb3B6eWV6a2x6emtrdmt4Z2F2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYzNjgxMTEsImV4cCI6MjA3MTk0NDExMX0.Kfn0KzpsZ_7BCmFBo7xAxas7zs6CGwMY3nNhB7pzFcM";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void main(String[] args) {
        SQLTest test = new SQLTest();
        try {
            test.testSqlFunction();
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("DataBase")
    public void testSqlFunction() {
        try {
            String query = "SELECT id, created_at, email FROM users WHERE discount > 0 ORDER BY created_at DESC LIMIT 3";
            String sqlBody = String.format("{\"query_text\": \"%s\"}", query);

            System.out.println("SQL: " + query);

            HttpResponse<String> response = executeSqlRequest(sqlBody);
            printResponse(response);

        } catch (Exception e) {
            System.err.println("Ошибка при выполнении SQL запроса: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private HttpResponse<String> executeSqlRequest(String body) throws Exception {
        String url = SUPABASE_URL + "/rpc/exec_sql";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", API_KEY)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void printResponse(HttpResponse<String> response) {
        System.out.println("Status: " + response.statusCode());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.println("✅ Успех! Данные:");
            System.out.println(formatJson(response.body()));
        } else {
            System.out.println("❌ Ошибка! Ответ:");
            System.out.println(response.body());
        }
        System.out.println("\n" + "─".repeat(70) + "\n");
    }

    private String formatJson(String json) {
        try {
            return json.replace("},{", "},\n{")
                    .replace("[{", "[\n{")
                    .replace("}]", "}\n]")
                    .replace("}, {", "},\n{");
        } catch (Exception e) {
            System.err.println("Ошибка при форматировании JSON: " + e.getMessage());
            return json;
        }
    }
}