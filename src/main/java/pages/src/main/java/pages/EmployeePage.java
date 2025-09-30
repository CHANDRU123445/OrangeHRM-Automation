
package Pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class EmployeePage {

    WebDriver driver;
    WebDriverWait wait;
    private List<String> addedEmployeeNames = new ArrayList<>(); // store added employees

    public EmployeePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // Locators
    By pimMenu = By.xpath("//a[contains(text(),'PIM') or contains(@href,'pim')]");
    By addEmployeeBtn = By.xpath("//a[contains(text(),'Add Employee')]");
    By employeeListMenu = By.xpath("//a[contains(text(),'Employee List')]");
    By firstNameInput = By.name("firstName");
    By lastNameInput = By.name("lastName");
    By saveButton = By.cssSelector("button[type='submit']");
    By personalDetailsHeader = By.xpath("//h6[text()='Personal Details']"); // appears after save
    By employeeTableRows = By.xpath("//div[@class='oxd-table-body']//div[@role='row']");
    By searchNameInput = By.xpath("//input[contains(@placeholder,'Type for hints...')]");
    By searchBtn = By.xpath("//button[@type='submit']");
    By userDropdown = By.xpath("//span[@class='oxd-userdropdown-tab']");
    By logoutBtn = By.xpath("//a[text()='Logout']");
    By loader = By.cssSelector("div.oxd-form-loader"); // loader overlay

    // Navigate to PIM module
    public void navigateToPIM() {
        wait.until(ExpectedConditions.elementToBeClickable(pimMenu)).click();
        waitForLoaderToDisappear();
    }

    // Click Add Employee button
    public void clickAddEmployee() {
        wait.until(ExpectedConditions.elementToBeClickable(addEmployeeBtn)).click();
        waitForLoaderToDisappear();
    }

    // Wait for loader overlay to disappear
    private void waitForLoaderToDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loader));
        } catch (Exception e) {
            // ignore if loader not present
        }
    }

    // Click element using JavaScript
    private void clickElementJS(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

 // Add single employee
    public String addEmployee(String firstName, String lastName) {
        try {
            navigateToPIM();
            clickAddEmployee();

            wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput)).sendKeys(firstName);
            wait.until(ExpectedConditions.visibilityOfElementLocated(lastNameInput)).sendKeys(lastName);

            waitForLoaderToDisappear();

            WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
            try {
                saveBtn.click(); // normal click
            } catch (Exception e) {
                clickElementJS(saveBtn); // fallback JS click
            }

            // ✅ Wait for "Personal Details" page after save
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h6[text()='Personal Details']")));

            String fullName = firstName + " " + lastName;
            addedEmployeeNames.add(fullName);
            System.out.println("✅ Employee added: " + fullName);
            return fullName;

        } catch (Exception e) {
            throw new RuntimeException("Failed to add employee: " + firstName + " " + lastName, e);
        }
    }


    // Add multiple employees
    public void addMultipleEmployees(String[][] employees) {
        for (String[] emp : employees) {
            addEmployee(emp[0], emp[1]);
        }
    }

    // Navigate to Employee List
    public void navigateToEmployeeList() {
        wait.until(ExpectedConditions.elementToBeClickable(employeeListMenu)).click();
        wait.until(ExpectedConditions.urlContains("viewEmployeeList"));
        waitForLoaderToDisappear();
    }

    // Search for employee and verify
    public boolean searchAndVerifyEmployee(String fullName) {
        try {
            navigateToEmployeeList();

            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(searchNameInput));
            searchBox.clear();
            searchBox.sendKeys(fullName);

            WebElement searchBtnEl = wait.until(ExpectedConditions.elementToBeClickable(searchBtn));
            clickElementJS(searchBtnEl);

            waitForLoaderToDisappear();

            List<WebElement> rows = driver.findElements(employeeTableRows);
            for (WebElement row : rows) {
                if (row.getText().contains(fullName)) {
                    System.out.println("✅ Name Verified: " + fullName);
                    scrollToElement(row);
                    takeScreenshot(fullName.replace(" ", "_") + ".png");
                    return true;
                }
            }

            System.out.println("❌ Employee not found: " + fullName);
            return false;
        } catch (Exception e) {
            System.out.println("⚠️ Error while searching for: " + fullName + " -> " + e.getMessage());
            return false;
        }
    }

    // Scroll to element
    private void scrollToElement(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();
    }

    // Take screenshot
    public void takeScreenshot(String fileName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File("screenshots/" + fileName);
            destFile.getParentFile().mkdirs();
            FileUtils.copyFile(screenshot, destFile);
            System.out.println("📸 Screenshot saved: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("⚠️ Screenshot failed: " + e.getMessage());
        }
    }

    // Search all added employees one by one
    public void searchScrollScreenshotResetAllEmployees(String[][] employees) throws Exception {
        for (String[] emp : employees) {
            String fullName = emp[0] + " " + emp[1];
            boolean found = searchAndVerifyEmployee(fullName);
            if (!found) {
                throw new AssertionError("Employee not found: " + fullName);
            }

            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(searchNameInput));
            searchBox.clear();
        }
    }
    // ✅ Verify all employees added are present in Employee List
    public void verifyAllAddedEmployeesInList() {
        navigateToEmployeeList();

        for (String fullName : addedEmployeeNames) {
            boolean found = false;

            try {
                WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(searchNameInput));
                searchBox.clear();
                searchBox.sendKeys(fullName);

                WebElement searchBtnEl = wait.until(ExpectedConditions.elementToBeClickable(searchBtn));
                clickElementJS(searchBtnEl);

                waitForLoaderToDisappear();

                List<WebElement> rows = driver.findElements(employeeTableRows);
                for (WebElement row : rows) {
                    if (row.getText().contains(fullName)) {
                        System.out.println("🎯 Name Verified in Employee List: " + fullName);
                        scrollToElement(row);
                        takeScreenshot(fullName.replace(" ", "_") + "_verified.png");
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    throw new AssertionError("❌ Employee not found in Employee List: " + fullName);
                }

            } catch (Exception e) {
                throw new RuntimeException("⚠️ Error verifying employee: " + fullName, e);
            }
        }
    }

    // Logout
    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(userDropdown)).click();
        wait.until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();
    }

    // Getter for added employees
    public List<String> getAddedEmployeeNames() {
        return addedEmployeeNames;
    }

	public void verifyAllAddedEmployees() {
		// TODO Auto-generated method stub
		
	}

	public void verifyEmployeesInList() {
		// TODO Auto-generated method stub
		
	}
}

