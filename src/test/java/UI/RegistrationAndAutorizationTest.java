package UI;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegistrationAndAutorizationTest extends TestData {

    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Main Page")
    @DisplayName("Перейти на главную страницу и проверить, что страница загрузилась")
    public void OpenHomePageTest() {
        TestData.getDriver().get(HomePage.homeUrl);
        TestData.getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10L));
        System.out.println(TestData.getDriver().findElement(By.xpath(HomePage.homePageTextPath)).getText());
        assert TestData.getDriver().findElement(By.xpath(HomePage.homePageTextPath)).getText().equals("Это ТЕСТОВЫЙ магазин! Осторожно, тут могут быть баги :) ");
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Registration")
    @DisplayName("Регистрация позитивный тест")
    public void GoToRegisterPageTest() {
        TestData.getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5L));
        TestData.getDriver().findElement(By.xpath(HomePage.signUpButtonXPath)).click();
        System.out.println(TestData.getDriver().findElement(By.xpath(SignUpPage.registerHeaderTextPath)).getText());
        assert TestData.getDriver().findElement(By.xpath(SignUpPage.registerHeaderTextPath)).getText().equals("""
            Главная
            Регистрация""");
        TestData.getDriver().findElement(By.xpath(SignUpPage.userNameFieldXPath)).sendKeys(getUserName());
        TestData.getDriver().findElement(By.xpath(SignUpPage.userEmailFieldPath)).sendKeys(getEmail());
        TestData.getDriver().findElement(By.xpath(SignUpPage.newPasswordFieldXPath)).sendKeys(getPassword());
        TestData.getDriver().findElement(By.xpath(SignUpPage.repeatPasswordFieldXPath)).sendKeys(getPassword());
        TestData.getDriver().findElement(By.xpath(SignUpPage.buttonSignInPath)).click();
        TestData.getDriver().findElement(By.xpath(SignUpPage.buttonClosePath)).click();

    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Autorization")
    @DisplayName("Авторизация позитивный тест")
    public void RegisterPositiveTest() throws InterruptedException {
        TestData.getDriver().get(HomePage.homeUrl);
        TestData.getDriver().findElement(By.xpath(HomePage.logInButtonXPath)).click();
        System.out.println(TestData.getDriver().findElement(By.xpath(SignUpPage.registerHeaderTextPath)).getText());
        assert TestData.getDriver().findElement(By.xpath(SignUpPage.registerHeaderTextPath)).getText().equals("""
            Главная
            Авторизация""");

        TestData.getDriver().findElement(By.xpath(LoginPage.loginFieldXPath)).sendKeys(getEmail());
        TestData.getDriver().findElement(By.xpath(LoginPage.enterPasswordFieldXPath)).sendKeys(getPassword());

        TestData.getDriver().findElement(By.xpath(LoginPage.buttonLoginPath)).click();
        TestData.getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5L));

        System.out.println(TestData.getDriver().findElement(By.xpath(HomePage.authorisedUserName)).getText());
        assert TestData.getDriver().findElement(By.xpath(HomePage.authorisedUserName)).getText().equals(TestData.getUserName());

    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Autorization")
    @DisplayName("Выход из аккаунта позитивный тест")
    public void QuitPositiveTest() throws InterruptedException {

        TestData.getDriver().findElement(By.xpath(HomePage.authorisedUserName)).click();
        TestData.getDriver().findElement(By.xpath(HomePage.logOutButton)).click();
        assert TestData.getDriver().findElement(By.xpath(SignUpPage.buttonSignInPath)).isDisplayed();
    }
}
