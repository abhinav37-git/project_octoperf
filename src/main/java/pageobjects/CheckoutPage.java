package pageobjects;

import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutPage {
	WebDriver driver;
	WebDriverWait wait;
	
	@FindBy(name="newOrder")
	private WebElement continueBtn;
	
	@FindBy(css = "a.Button[href*='confirmed=true']")
	private WebElement confirmBtn;

	
	@FindBy(xpath="//th[contains(text(), 'Payment Details')]")
	private WebElement paymentDetailsHeader;
	
	@FindBy(css="ul.messages li")
	private WebElement thankYouMessage;
	
	@FindBy(css="#BackLink a[href*='Catalog.action']")
	private WebElement returnToMainMenuLink;
	
	public CheckoutPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}
	
	public void clickContinueOnBilling(){
	    int attempts = 0;
	    while (attempts < 3) {
	        try {
	            wait.until(ExpectedConditions.visibilityOf(continueBtn));
	            wait.until(ExpectedConditions.elementToBeClickable(continueBtn));
	            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", continueBtn);
	            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", continueBtn);
	            return;
	        } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
	            System.out.println("Retrying click on Continue (billing)... Attempt: " + (attempts + 1));
	            PageFactory.initElements(driver, this); // re-bind element
	            attempts++;
	            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
	        }
	    }
	    throw new RuntimeException("Failed to click 'Continue' on Billing after 3 attempts.");
	}

	
	public void confirmOrder() {
	    int attempts = 0;
	    while (attempts < 3) {
	        try {
	            wait.until(ExpectedConditions.visibilityOf(confirmBtn));
	            wait.until(ExpectedConditions.elementToBeClickable(confirmBtn));

	            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", confirmBtn);
	            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmBtn);

	            return;
	        } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
	            System.out.println("Retrying confirm click due to exception: " + e.getClass().getSimpleName());
	            PageFactory.initElements(driver, this); // Re-bind WebElements
	            attempts++;
	            try {
	                Thread.sleep(500);
	            } catch (InterruptedException ignored) {}
	        }
	    }
	    throw new RuntimeException("Failed to click Confirm button after retries");
	}

	
	public boolean isThankYouMessageDisplayed() {
	    try {
	        By thankYouLocator = By.cssSelector("ul.messages li");
	        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(thankYouLocator));
	        return message.getText().contains("Thank you");
	    } catch (Exception e) {
	        System.out.println("Thank you message not found: " + e.getMessage());
	        return false;
	    }
	}

	
	public boolean isPaymentDetailsVisible() {
		// Consider either a Payment Details header OR presence of Continue button 
		try {
			if (wait.until(ExpectedConditions.visibilityOf(continueBtn)).isDisplayed()) {
				return true;
			}
		} catch (Exception ignored) {}
		
		By[] headerCandidates = new By[] {
			By.xpath("//th[contains(normalize-space(.), 'Payment Details')]"),
			By.xpath("//h2[contains(normalize-space(.), 'Payment Details')]"),
			By.cssSelector("#Catalog th"),
			By.cssSelector("#Catalog h2")
		};
		for (By locator : headerCandidates) {
			try {
				WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
				if (header != null && header.isDisplayed()) return true;
			} catch (Exception ignored) {}
		}
		return false;
	}
	
	public void returnToMainMenu() {
	    int attempts = 0;
	    while (attempts < 3) {
	        try {
	            By menuLink = By.xpath("//a[contains(text(), 'Return to Main Menu')]");
	            WebElement mainMenu = wait.until(ExpectedConditions.elementToBeClickable(menuLink));
	            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", mainMenu);
	            mainMenu.click();
	            return;
	        } catch (StaleElementReferenceException | TimeoutException | ElementClickInterceptedException e) {
	            System.out.println("Retrying Return to Main Menu due to: " + e.getClass().getSimpleName());
	            attempts++;
	            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
	        }
	    }
	    throw new RuntimeException("Failed to click Return to Main Menu after retries.");
	}

	public void signOut() {
	    By signOutLinkBy = By.partialLinkText("Sign Out");
	    int attempts = 0;
	    while (attempts < 2) {
	        try {
	            WebElement signOutLink = driver.findElement(signOutLinkBy);
	            signOutLink.click();
	            System.out.println("User signed out successfully");
	            break;  // exit loop if successful
	        } catch (StaleElementReferenceException e) {
	            attempts++;
	            System.out.println("StaleElementReferenceException caught. Retrying... Attempt " + attempts);
	            if (attempts == 2) {
	                System.out.println("Failed to sign out due to stale element.");
	                throw e;
	            }
	        } catch (Exception e) {
	            System.out.println("Failed to sign out: " + e.getMessage());
	            throw e;
	        }
	    }
	}




}
