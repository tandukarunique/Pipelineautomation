package pipelineStater;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import io.github.bonigarcia.wdm.WebDriverManager;
import pipelinestater.plLoginpage;

import java.time.Duration;

public class BaseTest {
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor js;
    protected plLoginpage loginPage;
    
    protected static final String EMAIL = "uniquetandukar8645@gmail.com";
    protected static final String PASSWORD = "Tha chaina 098!";
    protected static final String BASE_URL = "https://dev.chatboq.com";
    
    @BeforeClass(alwaysRun = true)  // ← CHANGED from @BeforeMethod to @BeforeClass
    @Parameters({"browser", "headless"})
    public void setUp(String browser, String headless) throws Exception {
        System.out.println("\n\n");
        System.out.println("Setting up WebDriver for: " + browser);
        System.out.println("========================================");
        
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
            "--disable-notifications",
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--remote-allow-origins=*"
        );
        
        if (Boolean.parseBoolean(headless)) {
            options.addArguments("--headless");
            options.addArguments("--window-size=1920,1080");
        }
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        js = (JavascriptExecutor) driver;
        loginPage = new plLoginpage(driver);
        
        // Login ONCE here
      System.out.println("Ensuring user is logged in...");
     boolean authLoaded = loginPage.loadSavedAuth();
       
        
        if (!authLoaded) {
            loginPage.manualLoginWithCaptcha(EMAIL, PASSWORD);
        }
      loginPage.gotoAuthenticated("/dashboard", ".*\\/dashboard.*");
       System.out.println("✅ Dashboard reached!");
        
        Thread.sleep(2000);
    }
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        System.out.println("\n");
        System.out.println("Closing WebDriver...");
        System.out.println("\n");
        if (driver != null) {
           driver.quit();
        }
    }
}