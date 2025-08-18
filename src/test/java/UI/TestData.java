package UI;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;
import org.junit.jupiter.api.BeforeAll;
import java.awt.Dimension;
import java.awt.Toolkit;


public class TestData implements WebDriverListener {

    private static String userName;
    private static String password;
    public static String email;
    private static WebDriver decoratedDriver;

    public static void setUserName() {
        userName = "UserName117";
    }

    public static void setEmail() {
        email = "aaa117@aaa.com";
    }

    public static void setPassword() {
        password = "Password1!";
    }

    public static String getUserName() {
        return userName;
    }

    public static String getEmail() {
        return email;
    }

    public static String getPassword() {
        return password;
    }


    @BeforeAll
    public static void setUp() {
        setUserName();
        setEmail();
        setPassword();

        // Инициализация ChromeOptions
        ChromeOptions options = new ChromeOptions();
        // Добавление расширения (убедитесь, что путь правильный)
//        options.addExtensions(new File("E:/Prog/..."));

        // WebDriver компоненты
        WebDriver originalDriver = new ChromeDriver(options);
        TestData testDataListener = new TestData();
        EventFiringDecorator<@NotNull WebDriver> decorator = new EventFiringDecorator<>(testDataListener);
        decoratedDriver = decorator.decorate(originalDriver);
        // Получаем размер экрана
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        // Устанавливаем размер окна
        decoratedDriver.manage().window().setSize(new org.openqa.selenium.Dimension(width, height));
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