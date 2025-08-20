package pageobjects;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import static org.testng.Assert.*;

public class SearchPage {
    private static final Logger logger = LogManager.getLogger(SearchPage.class);
    WebDriver driver;
    WebDriverWait wait;

    public SearchPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void searchProduct(String searchTerm) {
        logger.info("Searching for: {}", searchTerm);

        try {
            WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("keyword")));
            assertNotNull(searchInput, "Search input not found");
            searchInput.clear();

            // Use JavascriptExecutor to set the input value
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].value = arguments[1];", searchInput, searchTerm);
            // Trigger input event to notify any JS listeners
            js.executeScript("arguments[0].dispatchEvent(new Event('input'));", searchInput);

            logger.info("Entered search term into input using JavaScript");

            WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.name("searchProducts")));
            assertNotNull(searchButton, "Search button not found");

            // Use JavaScript to click the button instead of WebElement.click()
            js.executeScript("arguments[0].click();", searchButton);
            logger.info("Clicked search button using JavaScript");

            // Wait for results to appear
            WebElement catalog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Catalog")));
            assertTrue(catalog.isDisplayed(), "Catalog not visible after search");
            logger.info("Catalog is visible — search successful");

        } catch (StaleElementReferenceException e) {
            logger.error("StaleElementReferenceException during searchProduct", e);
            throw new RuntimeException("Element became stale during search", e);
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for search input/results", e);
            throw new RuntimeException("Search input or results not visible", e);
        } catch (Exception e) {
            logger.error("Unexpected error during search", e);
            throw e;
        }
    }


    public void openFirstSearchResultProduct() {
        logger.info("Opening first product from search results");

        By catalogLocator = By.id("Catalog");
        By productLinkLocator = By.cssSelector("a[href*='viewProduct'], a[href*='productId=']");

        int attempts = 0;
        final int maxAttempts = 3;

        JavascriptExecutor js = (JavascriptExecutor) driver;

        while (attempts < maxAttempts) {
            try {
                // Wait for catalog to be visible
                WebElement catalog = wait.until(ExpectedConditions.visibilityOfElementLocated(catalogLocator));
                // Find all product links inside catalog fresh
                java.util.List<WebElement> productLinks = catalog.findElements(productLinkLocator);

                if (productLinks.isEmpty()) {
                    throw new NoSuchElementException("No product links found inside catalog");
                }

                WebElement firstProductLink = productLinks.get(0);

                // Scroll element into view
                js.executeScript("arguments[0].scrollIntoView(true);", firstProductLink);

                // Click the first product link using JavaScript
                js.executeScript("arguments[0].click();", firstProductLink);

                logger.info("Clicked first product link successfully (using JavaScript)");
                return; // exit method on success

            } catch (StaleElementReferenceException | NoSuchElementException e) {
                attempts++;
                logger.warn("Stale element or no product found, retrying attempt " + attempts);
                try {
                    Thread.sleep(500); // small wait before retrying
                } catch (InterruptedException ignored) {}
            }
        }

        throw new TimeoutException("Failed to click the first product link after " + maxAttempts + " attempts");
    }


    public boolean isProductPageOpened() {
        try {
            WebElement productHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#Catalog h2")));
            return productHeader.isDisplayed();
        } catch (TimeoutException e) {
            logger.warn("Product page not opened in expected time");
            return false;
        }
    }
}
