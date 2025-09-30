package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.File;

public class BaseTest {

    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver == null) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
        }
        return driver;
    }

    public static void openApplication(String url) {
        getDriver().get(url);
        createScreenshotsFolder();
    }

    public static void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private static void createScreenshotsFolder() {
        File dir = new File("Screenshots");
        if (!dir.exists()) {
            dir.mkdir();
            System.out.println("📁 Created Screenshots folder");
        }
    }

    public static void quitDriver() {
        closeBrowser();
    }
}


