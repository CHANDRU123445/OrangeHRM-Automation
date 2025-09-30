package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By usernameField = By.name("username");
    private By passwordField = By.name("password");
    private By loginButton   = By.xpath("//button[@type='submit']");
    private By dashboardText = By.xpath("//h6[text()='Dashboard']");

    public LoginPage(WebDriver driver) {
        if(driver == null) throw new IllegalArgumentException("WebDriver cannot be null!");
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField)).sendKeys(username);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField)).sendKeys(password);
    }

    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    public boolean isLoginSuccessful() {
        try {
            WebElement dashboard = wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardText));
            return dashboard.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
