package pageobjects;

import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage {
	
	private static final Logger logger = LogManager.getLogger(LoginPage.class);
	WebDriver driver;
	WebDriverWait wait;
	
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
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}


	public void login(String user, String pass) {
		logger.info("Attempting login with username: "+user);
		try {
			if (!isSignInFormVisible()) {
				clickSignInLink.click();
				logger.debug("Entered sign in page");
			}
		} catch (Exception ignore) {
			// If link is not present, assume we are already on sign-in page
		}

		// Wait for fields and button to be ready
		WebElement userField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
		WebElement passField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
		WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.name("signon")));

		if (user != null) {
			userField.click();
			userField.clear();
			userField.sendKeys(user);
		}
		passField.click();
		passField.clear();
		passField.sendKeys(pass);
		submitBtn.click();
		
		logger.info("User signed into JpetStore");
	}

	public boolean isSignInFormVisible() {
		try {
			return username.isDisplayed() && password.isDisplayed();
		} catch (Exception e) {
			return false;
		}
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
