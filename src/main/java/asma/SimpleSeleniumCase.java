package asma;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.logging.Level;


public class SimpleSeleniumCase {
    public WebDriver driver;


    public ChromeDriverService service;

    @Before
    public void setUp() {
        try {

            System.setProperty("webdriver.chrome.driver",System.getenv().get("ASMA_DRIVER_PATH"));

            ChromeOptions options = new ChromeOptions();
            options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

            options.setBinary(System.getenv().get("ASMA_CHROME_BIN"));
            options.setHeadless(false);

            options.addArguments("--remote-debugging-port=9222");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");

            //Step 1- Driver Instantiation: Instantiate driver object as ChromeDriver

            driver = new ChromeDriver(options);
            ((ChromeDriver) driver).setLogLevel(Level.ALL);

            // driver.getErrorHandler().setIncludeServerErrors(true);
            // driver.get("about:blank");
            // driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

            // driver.setErrorHandler(err);

        } catch (Throwable t) {
            System.out.println(t.getMessage());
            throw new RuntimeException("Failed to start chromedriver",t);
        }


    }

    @Test
    public void LoadHome() {


        driver.get("https://www.apica.io");
        Assert.assertTrue("title check failed", driver.getTitle().toLowerCase().contains("apica"));


    }


    @Test
    public void ClickDemo()
            throws InterruptedException {

        //Step 2- Navigation: Open a website
        driver.navigate().to("https://www.apicasystems.com/");
        //Step 3- Assertion: Check its title is correct
        //assertEquals method Parameters: Expected Value, Actual Value, Assertion Message
        Assert.assertTrue("title check failed", driver.getTitle().toLowerCase().contains("apica"));


    }


    @After
    public void tearDown() {
        if ( driver != null )
            driver.quit();
    }


}


