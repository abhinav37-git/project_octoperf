package pageobjects;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage {
	
	WebDriver driver;
	
	@FindBy(linkText="Enter the Store")
	private WebElement enterStoreLink;
	
	public HomePage(WebDriver driver) {
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterStore() {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	    try {
	        WebElement enterLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Enter the Store")));
	        enterLink.click();
	    } catch (Exception e) {
	        // Fallback to JS click if normal click fails
	        WebElement enterLink = driver.findElement(By.linkText("Enter the Store"));
	        JavascriptExecutor js = (JavascriptExecutor) driver;
	        js.executeScript("arguments[0].click();", enterLink);
	    }
	}

}
