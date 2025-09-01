package API.SQL;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SQLTest {

    private final SQLExecutor sqlExecutor = new SQLExecutor();

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("DataBase")
    @DisplayName("Вывод данных по пользователям с максимальной скидкой")
    public void testSqlDiscount() {
        try {
            String query = "SELECT id, created_at, email, discount FROM users WHERE discount = (Select MAX(discount) FROM users GROUP BY discount LIMIT 1) ORDER BY created_at DESC";

            System.out.println("SQL: " + query);

            HttpResponse<String> response = sqlExecutor.executeSqlRequest(query);
            sqlExecutor.printResponse(response);

            // Проверка успешного статуса
            assertTrue(response.statusCode() >= 200 && response.statusCode() < 300,
                    "HTTP статус должен быть успешным");

        } catch (Exception e) {
            System.err.println("Ошибка при выполнении SQL запроса: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Тест не прошел", e);
        }
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("DataBase")
    @DisplayName("Вывод данных по эффективности акций для продуктов с истекающими сроками годности")
    public void testSqlSalesEfficiency() {
        try {
            String query = "SELECT sale_id, " +
                    "products.name, " +
                    "sales.discount, " +
                    "stock_balance, " +
                    "SUM(quantity) AS quantity_sold, " +
                    "beginning_date, " +
                    "end_date " +
                    "FROM products JOIN sales ON products.product_id = sales.product_id " +
                            "JOIN purchases ON products.product_id = purchases.product_id " +
                    "WHERE expires_at > beginning_date " +
                        "AND expires_at <= end_date + INTERVAL '1 day' " +
                        "AND stock_balance<20 " +
                    "GROUP BY quantity, sale_id, products.name, sales.discount, stock_balance, expires_at " +
                    "ORDER BY quantity DESC";

            System.out.println("SQL: " + query);

            HttpResponse<String> response = sqlExecutor.executeSqlRequest(query);
            sqlExecutor.printResponse(response);

            // Проверка успешного статуса
            assertTrue(response.statusCode() >= 200 && response.statusCode() < 300,
                    "HTTP статус должен быть успешным");

        } catch (Exception e) {
            System.err.println("Ошибка при выполнении SQL запроса: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Тест не прошел", e);
        }
    }


    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("DataBase")
    @DisplayName("Вывод анализа покупок за последние 30 дней")
    public void testSqlUserPurchaseAnalysis() {
        try {
            String query = "SELECT " +
                    "users.email, " +
                    "users.discount, " +
                    "products.name, " +
                    "products.price, " +
                    "sales.sale_id, " +
                    "sales.discount, " +
                    "purchases.id, " +
                    "purchases.date, " +
                    "purchases.quantity, " +
                    "purchases.price, " +
                    "purchases.discount as total_discount, " +
                    "SUM(purchases.quantity) OVER (PARTITION BY users.id) as total_user_purchases, " +
                    "SUM(purchases.quantity * purchases.price) OVER (PARTITION BY users.id) as total_user_spent, " +
                    "AVG(purchases.quantity) OVER (PARTITION BY products.product_id) as avg_quantity_per_product " +
                    "FROM users " +
                    "INNER JOIN purchases ON users.id = purchases.buyer " +
                    "INNER JOIN products ON purchases.product_id = products.product_id " +
                    "LEFT JOIN sales ON products.product_id = sales.product_id " +
                    "WHERE purchases.date >= CURRENT_DATE - INTERVAL '30 days' " +
                    "ORDER BY total_user_spent DESC, users.id, purchases.date DESC";


            System.out.println("SQL: " + query);

            HttpResponse<String> response = sqlExecutor.executeSqlRequest(query);
            sqlExecutor.printResponse(response);

            // Проверка успешного статуса
            assertTrue(response.statusCode() >= 200 && response.statusCode() < 300,
                    "HTTP статус должен быть успешным");

        } catch (Exception e) {
            System.err.println("Ошибка при выполнении SQL запроса: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Тест не прошел", e);
        }
    }
}