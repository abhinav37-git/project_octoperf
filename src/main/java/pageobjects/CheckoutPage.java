package pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CheckoutPage {
	WebDriver driver;
	
	@FindBy(name="newOrder")
	private WebElement continueBtn;
	
	@FindBy(css="#a.Button[href*='confirmed=true']")
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
	}
	
	public void clickContinueOnBilling() {
		continueBtn.click();
	}
	
	public void confirmOrder() {
		confirmBtn.click();
	}
	
	public boolean isThankYouMessageDisplayed() {
		return thankYouMessage.isDisplayed();
	}
	
	public boolean isPaymentDetailsVisible() {
		try {
			return paymentDetailsHeader.isDisplayed();
		}catch(Exception e) {
			return false;
		}
	}
	
	public void returnToMainMenu() {
		returnToMainMenuLink.click();
	}

}
