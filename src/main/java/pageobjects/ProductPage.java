package pageobjects;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


public class ProductPage {
	
	WebDriver driver;
	
	@FindBy(css ="#Catalog a.Button[href*='addItemToCart']")
	WebElement addToCartBtn;
	
	@FindBy(css ="#Cart a.Button[href*='newOrderForm']")
	WebElement proceedToCheckOutBtn;
	
	
	public ProductPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void checkout() {
		proceedToCheckOutBtn.click();
	}
	
	public void addToCart() {
		addToCartBtn.click();
	}
}
