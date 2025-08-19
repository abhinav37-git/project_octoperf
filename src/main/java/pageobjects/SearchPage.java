package pageobjects;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchPage {
    private static final Logger logger = LogManager.getLogger(SearchPage.class);
    WebDriver driver;
    WebDriverWait wait;

    @FindBy(xpath = "//input[@name='keyword']")
    WebElement searchInput;

    @FindBy(xpath = "//input[@name='searchProducts']")
    WebElement searchBtn;

    // Keep no hard-coded product id; we'll resolve dynamically in code

    @FindBy(css = "#Catalog h2")
    private WebElement productHeader;

    @FindBy(css = "a[href*='Catalog.action']")
    private WebElement catalogHomeLink;

    public SearchPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void searchProduct(String searchkey) {
        try {
            wait.until(ExpectedConditions.visibilityOf(searchInput));
        } catch (TimeoutException e) {
            try {
                catalogHomeLink.click();
                wait.until(ExpectedConditions.visibilityOf(searchInput));
            } catch (Exception ignored) {
                // fall through, will throw below if still not visible
            }
        }
        searchInput.clear();
        logger.info("Searching for item: " + searchkey);
        searchInput.sendKeys(searchkey);

        wait.until(ExpectedConditions.elementToBeClickable(searchBtn));
        searchBtn.click();

        logger.debug("Search submitted");
    }

    public void openFirstSearchResultProduct() {
        logger.info("Waiting for and opening first product from search results");

        // Ensure results loaded
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Catalog")));
        } catch (TimeoutException e) {
            throw new TimeoutException("Search results container not visible");
        }

        By[] candidateLocators = new By[] {
                By.cssSelector("#Catalog a[href*='viewProduct']"),
                By.cssSelector("#Catalog a[href*='productId=']"),
                By.xpath("//div[@id='Catalog']//a[contains(@href,'viewProduct')][1]"),
                By.xpath("//div[@id='Catalog']//a[contains(@href,'productId=')][1]")
        };

        WebElement linkToClick = null;
        for (By locator : candidateLocators) {
            try {
                linkToClick = wait.until(ExpectedConditions.elementToBeClickable(locator));
                if (linkToClick != null) break;
            } catch (Exception ignored) {
                // try next locator
            }
        }

        if (linkToClick == null) {
            throw new TimeoutException("No product link found in search results");
        }

        try {
            linkToClick.click();
        } catch (Exception e) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", linkToClick);
        }
    }

    public boolean isProductPageOpened() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(productHeader)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }
}
