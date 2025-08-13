package UI;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.time.Duration;

public class TestData implements WebDriverListener {
    private static String loginAdmin;
    private static String passwordAdmin;
    public static String urlVipolnenieBPBlockRuk;

    public static void setLoginAdmin() {
        loginAdmin = "liazgina-di";
    }

    public static void setPasswordAdmin() {
        passwordAdmin = ")$%5srZ8o688Dm#q";
    }

    public static void setUrlVipolnenieBPBlockRuk() {
        urlVipolnenieBPBlockRuk = "https://bi.rosseti.digital/viewer?dashboardGuid=d154a8eaf6824e2b9de1bb04dd061c18&sheetGuid=1305e8ce744049e1abdacea1ea397a26&fit=true";
    }

    public static String getLoginAdmin() {
        return loginAdmin;
    }

    public static String getPasswordAdmin() {
        return passwordAdmin;
    }

    // WebDriver компоненты
    private static final WebDriver originalDriver = new ChromeDriver();
    private static final TestData testDataListener = new TestData();
    private static final EventFiringDecorator<@NotNull WebDriver> decorator =
            new EventFiringDecorator<>(testDataListener);
    private static final WebDriver decoratedDriver = decorator.decorate(originalDriver);

    @BeforeAll
    public static void setUp() {
        setLoginAdmin();
        setPasswordAdmin();
        setUrlVipolnenieBPBlockRuk();
        decoratedDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10L));
        decoratedDriver.get(urlVipolnenieBPBlockRuk);
        decoratedDriver.findElement(By.xpath(LoginPage.loginFieldXPath))
                .sendKeys(getLoginAdmin());

        decoratedDriver.findElement(By.xpath(LoginPage.passwordFieldXPath))
                .sendKeys(getPasswordAdmin());

        decoratedDriver.findElement(By.xpath(LoginPage.buttonEnterXPath))
                .click();
        decoratedDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20L));
    }

    @AfterAll
    public static void closeDriver() {
        if (decoratedDriver != null) {
            decoratedDriver.quit();
        }
    }

    public static WebDriver getDriver() {
        return decoratedDriver;
    }

}