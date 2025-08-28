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


public class DriverDecorating implements WebDriverListener {

    public static WebDriver decoratedDriver;

    @BeforeAll
    public static void setUp() {

        // Инициализация ChromeOptions
        ChromeOptions options = new ChromeOptions();

        // WebDriver компоненты
        WebDriver originalDriver = new ChromeDriver(options);
        DriverDecorating driverDecoratingListener = new DriverDecorating();
        EventFiringDecorator<@NotNull WebDriver> decorator = new EventFiringDecorator<>(driverDecoratingListener);
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