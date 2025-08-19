package pageobjects;

import java.time.Duration;

import org.openqa.selenium.By;
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
	
	@FindBy(css="a.Button[href*='confirmed=true']")
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
	
	public void clickContinueOnBilling() {
		wait.until(ExpectedConditions.elementToBeClickable(continueBtn)).click();
	}
	
	public void confirmOrder() {
		wait.until(ExpectedConditions.elementToBeClickable(confirmBtn)).click();
	}
	
	public boolean isThankYouMessageDisplayed() {
		try {
			return wait.until(ExpectedConditions.visibilityOf(thankYouMessage)).isDisplayed();
		}catch(Exception e) {
			return false;
		}
	}
	
	public boolean isPaymentDetailsVisible() {
		// Consider either a Payment Details header OR presence of Continue button as page ready
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
		wait.until(ExpectedConditions.elementToBeClickable(returnToMainMenuLink)).click();
	}
}
