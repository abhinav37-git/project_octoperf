package pageobjects;

import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
	
	private static final Logger logger = LogManager.getLogger(LoginPage.class);
	WebDriver driver;
	
	@FindBy(partialLinkText="Sign In")
	WebElement clickSignInLink;
	
	@FindBy(name="username")
	WebElement username;
	
	@FindBy(name="password")
	WebElement password;
	
	@FindBy(name="signon")
	WebElement loginBtn;
	
	@FindBy(partialLinkText="Sign Out")
	private WebElement signOutLink;
	
	public LoginPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}


	public void login(String user, String pass) {
		logger.info("Attempting login with username: "+user);
		
		clickSignInLink.click();
		logger.debug("Entered sign in page");		
		//username.clear();
		
		username.sendKeys(user);
		password.clear();
		password.sendKeys(pass);
		loginBtn.click();
		
		logger.info("User signed into JpetStore");
	}
	
	public boolean isLoggedIn() {
		try {
			return signOutLink.isDisplayed();
		}catch(Exception e) {
			return false;
		}
	}
	
	public void signOut() {
		logger.info("User is signing out" );
		
		signOutLink.click();
		
		logger.info("signed out");
	}
}
