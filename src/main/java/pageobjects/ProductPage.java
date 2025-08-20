package pageobjects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class ProductPage {

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;

    @FindBy(css = "#Catalog a.Button[href*='addItemToCart']")
    WebElement addToCartBtn;

    @FindBy(css = "#Cart a.Button[href*='newOrderForm']")
    WebElement proceedToCheckOutBtn;

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    public void addToCart() {
        int attempts = 0;
        final int maxAttempts = 3;

        while (attempts < maxAttempts) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(addToCartBtn));
                js.executeScript("arguments[0].scrollIntoView(true);", addToCartBtn);
                js.executeScript("arguments[0].click();", addToCartBtn);
                return;
            } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                attempts++;
                System.out.println("Retrying addToCart... Attempt: " + attempts);
                PageFactory.initElements(driver, this); // rebind elements
                try {
                    Thread.sleep(500); // small pause before retry
                } catch (InterruptedException ignored) {}
            }
        }

        throw new RuntimeException("Failed to click Add to Cart after " + maxAttempts + " attempts");
    }

    public void checkout() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(proceedToCheckOutBtn));
            js.executeScript("arguments[0].scrollIntoView(true);", proceedToCheckOutBtn);
            js.executeScript("arguments[0].click();", proceedToCheckOutBtn);
        } catch (Exception e) {
            throw new RuntimeException("Checkout button click failed", e);
        }
    }
}
