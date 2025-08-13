package UI;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

public class AutorizationTest extends TestData {
    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Autorization")
    @DisplayName("Тестирование системы авторизации")
    public void AutorizationPositiveTest() {
        System.out.println(TestData.getDriver().findElement(By.xpath(VipolnenieBiznesPlanaBlockRuk.headerTextXPath)).getText());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Autorization")
    @DisplayName("Тестирование системы авторизации")
    public void VerifyPageLoadedTest() {
        assert TestData.getDriver().findElement(By.xpath(VipolnenieBiznesPlanaBlockRuk.headerTextXPath)).getText().equals("Выполнение бизнес-плана в части показателя «Уровень потерь электроэнергии»");

    }
}
