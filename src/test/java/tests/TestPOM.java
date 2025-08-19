package tests;

import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import pageobjects.CheckoutPage;
import pageobjects.HomePage;
import pageobjects.LoginPage;
import pageobjects.ProductPage;
import pageobjects.SearchPage;
import utils.ConfigReader;
import utils.TestDataProvider;

public class TestPOM {

    private static final Logger logger = LogManager.getLogger(TestPOM.class);
    WebDriver driver;

    HomePage home;
    LoginPage login;
    SearchPage search;
    ProductPage product;
    CheckoutPage checkout;

    private String username;
    private String password;

    @Parameters("browser")
    @BeforeClass
    public void setup(@Optional("chrome") String browser) {
        if (browser.equalsIgnoreCase("chrome")) {
            driver = new ChromeDriver();
        } else if (browser.equalsIgnoreCase("edge")) {
            driver = new EdgeDriver();
        } else {
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));  // increased to 10s
        driver.manage().window().maximize();
        driver.get(ConfigReader.get("url"));

        home = new HomePage(driver);
        login = new LoginPage(driver);
        search = new SearchPage(driver);
        product = new ProductPage(driver);
        checkout = new CheckoutPage(driver);
    }

    @Test(dataProvider = "loginData", dataProviderClass = TestDataProvider.class)
    public void testLogin(String user, String pass) {
        this.username = user;
        this.password = pass;

        home.enterStore();
        logger.info("Entered the store");

        login.login(user, pass);
        Assert.assertTrue(login.isLoggedIn(), "Login failed: Sign Out not visible");
        logger.info("Login successful");
    }

    @Test(dependsOnMethods = "testLogin")
    public void testSearchProduct() {
        search.searchProduct(ConfigReader.get("searchTerm"));
        search.openFirstSearchResultProduct();
        logger.info("Searched and opened product: " + ConfigReader.get("searchTerm"));
    }

    @Test(dependsOnMethods = "testSearchProduct")
    public void testAddToCart() {
        product.addToCart();
        logger.info("Product added to cart");
    }

    @Test(dependsOnMethods = "testAddToCart")
    public void testCheckout() {
        product.checkout();
        logger.info("Navigated to checkout");

        Assert.assertTrue(checkout.isPaymentDetailsVisible(), "Payment details not visible");
        checkout.clickContinueOnBilling();
        logger.info("Billing continued");
    }

    @Test(dependsOnMethods = "testCheckout")
    public void testConfirmOrder() {
        checkout.confirmOrder();
        logger.info("Order confirmed");

        Assert.assertTrue(checkout.isThankYouMessageDisplayed(), "Thank You message not displayed");
        logger.info("Order complete");
    }

    @Test(dependsOnMethods = "testConfirmOrder")
    public void testReturnToMainMenu() {
        checkout.returnToMainMenu();
        logger.info("Returned to main menu");
    }

    @Test(dependsOnMethods = "testReturnToMainMenu")
    public void testSignOut() {
        login.signOut();
        logger.info("Signed out successfully");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
