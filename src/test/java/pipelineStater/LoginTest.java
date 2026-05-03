package pipelineStater;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {
    
    @Test(priority = 1, description = "Verify login with saved authentication")
    public void testSavedAuthLogin() throws Exception {
        System.out.println("Test: Saved Authentication Login");
        
        boolean authLoaded = loginPage.loadSavedAuth();
        
        if (authLoaded) {
            loginPage.gotoAuthenticated("/dashboard", ".*\\/dashboard.*");
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("dashboard"), "Should be on dashboard page");
        } else {
            Assert.fail("No saved authentication found");
        }
    }
    
    // COMMENT OUT OR DELETE THIS ENTIRE TEST - It's deleting your auth.json!
    /*
    @Test(priority = 2, description = "Verify manual login with CAPTCHA")
    public void testManualLoginWithCaptcha() throws Exception {
        System.out.println("Test: Manual Login with CAPTCHA");
        
        java.io.File authFile = new java.io.File("auth.json");
        if (authFile.exists()) {
            authFile.delete();
            System.out.println("Deleted existing auth.json");
        }
        
        boolean result = loginPage.manualLoginWithCaptcha(EMAIL, PASSWORD);
        Assert.assertTrue(result, "Manual login should complete successfully");
    }
    */
    
    @Test(priority = 3, description = "Verify dashboard navigation")
    public void testNavigateToDashboard() throws Exception {
        System.out.println("Test: Navigate to Dashboard");
        
        loginPage.gotoAuthenticated("/dashboard", ".*\\/dashboard.*");
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("dashboard"), "Should be on dashboard page");
        
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[contains(@class,'rounded-full') and contains(@class,'h-8')]")
        )).click();
        
        System.out.println("Dashboard accessed successfully");
    }
}