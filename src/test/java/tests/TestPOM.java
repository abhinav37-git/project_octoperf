package tests;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

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

        // Ensure we are inside the store before any tests
        System.out.println("[STEP] Enter Store (Setup): START");
        home.enterStore();
        Assert.assertTrue(home.isHomePageLoaded(), "Home page did not load after entering the store (setup)");
        System.out.println("[STEP] Enter Store (Setup): END");
    }

    @Test(priority = 1, dataProvider = "loginData", dataProviderClass = TestDataProvider.class)
    public void testLogin(String user, String pass) {
        this.username = user;
        this.password = pass;
        System.out.println("[STEP] Sign In: START");
        login.login(user, pass);
        Assert.assertTrue(login.isLoggedIn(), "Login failed: Sign Out not visible");
        logger.info("Login successful");
        System.out.println("[STEP] Sign In: END");
    }

    @Test(priority = 2, dependsOnMethods = "testLogin")
    public void testSearchProduct() {
        System.out.println("[STEP] Search: START");
        search.searchProduct(ConfigReader.getSanitized("searchTerm"));
        search.openFirstSearchResultProduct();
        logger.info("Searched and opened product: " + ConfigReader.getSanitized("searchTerm"));
        Assert.assertTrue(search.isProductPageOpened(), "Product page did not open after clicking first result");
        System.out.println("[STEP] Search: END");
    }

    @Test(priority = 3, dependsOnMethods = "testSearchProduct")
    public void testAddToCart() {
        System.out.println("[STEP] Add To Cart: START");
        product.addToCart();
        logger.info("Product added to cart");
        System.out.println("[STEP] Add To Cart: END");
    }

    @Test(priority = 4, dependsOnMethods = "testAddToCart")
    public void testCheckout() {
        System.out.println("[STEP] Checkout: START");
        product.checkout();
        logger.info("Navigated to checkout");
        Assert.assertTrue(checkout.isPaymentDetailsVisible(), "Payment details not visible");
        checkout.clickContinueOnBilling();
        logger.info("Billing continued");
        System.out.println("[STEP] Checkout: END");
    }

    @Test(priority = 5, dependsOnMethods = "testCheckout")
    public void testConfirmOrder() {
        System.out.println("[STEP] Confirm Order: START");
        checkout.confirmOrder();
        Assert.assertTrue(checkout.isThankYouMessageDisplayed(), "Thank You message not displayed");
        System.out.println("[STEP] Confirm Order: END");
    }

    @Test(priority = 6, dependsOnMethods = "testConfirmOrder")
    public void testReturnToMainMenu() {
        System.out.println("[STEP] Return To Main Menu: START");
        checkout.returnToMainMenu();
        logger.info("Returned to main menu");
        Assert.assertTrue(home.isHomePageLoaded(), "Home page not visible after returning to main menu");
        System.out.println("[STEP] Return To Main Menu: END");
    }

    @Test(priority = 7, dependsOnMethods = "testReturnToMainMenu")
    public void testSignOut() {
        System.out.println("[STEP] Sign Out: START");
        checkout.signOut();
        System.out.println("Signed out successfully");
        Assert.assertTrue(home.isHomePageLoaded(), "Sign In link not visible after sign out");
        System.out.println("[STEP] Sign Out: END");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
