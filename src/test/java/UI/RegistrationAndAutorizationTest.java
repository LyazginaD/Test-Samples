package UI;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegistrationAndAutorizationTest extends DriverDecorating {

    public static String newId = "125";
    public static final String USER_NAME = String.format("UserName%s",newId);
    public static final String PASSWORD = "Password1!";
    public static final String EMAIL = String.format("aaa%s@aaa.com",newId);

    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Main Page")
    @DisplayName("Перейти на главную страницу и проверить, что страница загрузилась")
    public void OpenHomePageTest() {
        getDriver().get(HomePage.HOME_URL);
        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10L));
        System.out.println(DriverDecorating.getDriver().findElement(By.xpath(HomePage.HOME_PAGE_TEXT_PATH)).getText());
        assert DriverDecorating.getDriver().findElement(By.xpath(HomePage.HOME_PAGE_TEXT_PATH)).getText().equals("Это ТЕСТОВЫЙ магазин! Осторожно, тут могут быть баги :) ");
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Registration")
    @DisplayName("Регистрация позитивный тест")
    public void GoToRegisterPageTest() {
        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5L));
        getDriver().findElement(By.xpath(HomePage.SIGN_UP_BUTTON_X_PATH)).click();
        System.out.println(DriverDecorating.getDriver().findElement(By.xpath(SignUpPage.REGISTER_HEADER_TEXT_PATH)).getText());
        assert DriverDecorating.getDriver().findElement(By.xpath(SignUpPage.REGISTER_HEADER_TEXT_PATH)).getText().equals("""
            Главная
            Регистрация""");
        getDriver().findElement(By.xpath(SignUpPage.USER_NAME_FIELD_X_PATH)).sendKeys(USER_NAME);
        getDriver().findElement(By.xpath(SignUpPage.USER_EMAIL_FIELD_PATH)).sendKeys(EMAIL);
        getDriver().findElement(By.xpath(SignUpPage.NEW_PASSWORD_FIELD_X_PATH)).sendKeys(PASSWORD);
        getDriver().findElement(By.xpath(SignUpPage.REPEAT_PASSWORD_FIELD_X_PATH)).sendKeys(PASSWORD);
        getDriver().findElement(By.xpath(SignUpPage.BUTTON_SIGN_IN_PATH)).click();
        getDriver().findElement(By.xpath(SignUpPage.BUTTON_CLOSE_PATH)).click();

    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Autorization")
    @DisplayName("Авторизация позитивный тест")
    public void RegisterPositiveTest(){
        getDriver().get(HomePage.HOME_URL);
        getDriver().findElement(By.xpath(HomePage.LOG_IN_BUTTON_X_PATH)).click();
        System.out.println(DriverDecorating.getDriver().findElement(By.xpath(SignUpPage.REGISTER_HEADER_TEXT_PATH)).getText());
        assert DriverDecorating.getDriver().findElement(By.xpath(SignUpPage.REGISTER_HEADER_TEXT_PATH)).getText().equals("""
            Главная
            Авторизация""");

        getDriver().findElement(By.xpath(LoginPage.LOGIN_FIELD_X_PATH)).sendKeys(EMAIL);
        getDriver().findElement(By.xpath(LoginPage.ENTER_PASSWORD_FIELD_X_PATH)).sendKeys(PASSWORD);

        getDriver().findElement(By.xpath(LoginPage.BUTTON_LOGIN_PATH)).click();
        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10L));

        System.out.println(DriverDecorating.getDriver().findElement(By.xpath(HomePage.AUTHORISED_USER_NAME)).getText());
        assert DriverDecorating.getDriver().findElement(By.xpath(HomePage.AUTHORISED_USER_NAME)).getText().equals(USER_NAME);

    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Autorization")
    @DisplayName("Выход из аккаунта позитивный тест")
    public void QuitPositiveTest() {

        getDriver().findElement(By.xpath(HomePage.AUTHORISED_USER_NAME)).click();
        getDriver().findElement(By.xpath(HomePage.LOG_OUT_BUTTON)).click();
        assert DriverDecorating.getDriver().findElement(By.xpath(SignUpPage.BUTTON_SIGN_IN_PATH)).isDisplayed();
    }

    public static WebDriver getDriver() {
        return decoratedDriver;
    }
}
