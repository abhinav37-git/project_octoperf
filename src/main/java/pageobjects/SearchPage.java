package pageobjects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SearchPage {
    private static final Logger logger = LogManager.getLogger(SearchPage.class);
    WebDriver driver;
    WebDriverWait wait;

    @FindBy(xpath = "//input[@name='keyword']")
    WebElement searchInput;

    @FindBy(xpath = "//input[@name='searchProducts']")
    WebElement searchBtn;

    @FindBy(xpath = "//div[@id='Catalog']//a[contains(@href, 'FI-FW-02')]")
    private WebElement firstProductLink;

    public SearchPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void searchProduct(String searchkey) {
        wait.until(ExpectedConditions.visibilityOf(searchInput));
        searchInput.clear();
        logger.info("Searching for item: " + searchkey);
        searchInput.sendKeys(searchkey);

        wait.until(ExpectedConditions.elementToBeClickable(searchBtn));
        searchBtn.click();

        logger.debug("Search submitted");
    }

    public void openFirstSearchResultProduct() {
        logger.info("Waiting for and opening first product from search results");
        wait.until(ExpectedConditions.elementToBeClickable(firstProductLink));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", firstProductLink);
    }
}
