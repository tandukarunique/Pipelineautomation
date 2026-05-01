package pipelinestater;

import java.util.List;
import org.openqa.selenium.Keys;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import emailAutomate.Emailautomate;
import org.openqa.selenium.interactions.Actions;

public class plAgentlimit {

    private WebDriverWait wait;
    private WebDriver driver;
    private JavascriptExecutor js;
    private Emailautomate currentEmailAutomate;
    
    private static final int NUMBER_OF_INVITATIONS = 6;
    
    public plAgentlimit(WebDriverWait wait, WebDriver driver, JavascriptExecutor js, Emailautomate emailAutomate) throws InterruptedException {
        this.wait = wait;
        this.driver = driver;
        this.js = js;
        this.currentEmailAutomate = emailAutomate;
        executeInvitationProcess();
    }
    
    private void executeInvitationProcess() throws InterruptedException {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("SENDING " + NUMBER_OF_INVITATIONS + " INVITATIONS");
        System.out.println("=".repeat(70));
        
        for (int i = 1; i <= NUMBER_OF_INVITATIONS; i++) {
            System.out.println("\n[INVITATION " + i + " OF " + NUMBER_OF_INVITATIONS + "]");
            
            if (i > 1) {
                System.out.println("Opening new incognito email browser...");
                currentEmailAutomate.closeEmailBrowser();
                Thread.sleep(2000);
                currentEmailAutomate = new Emailautomate(true);
                Thread.sleep(2000);
            }
            
            resetAppState();
            
            String email = sendInvitation();
            if (email == null) continue;
            
            boolean accepted = acceptInvitation();
            System.out.println(accepted ? "SUCCESS: Invitation " + i + " completed" : "FAILED: Invitation " + i);
            
            if (i < NUMBER_OF_INVITATIONS) {
                System.out.println("Waiting 5 seconds...");
                Thread.sleep(5000);
            }
        }
        
        System.out.println("\nCompleted " + NUMBER_OF_INVITATIONS + " invitations");
    }
    
    private void resetAppState() throws InterruptedException {
        driver.navigate().refresh();
        Thread.sleep(3000);
        wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));
        
        try {
            wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/settings/account/information')]")
            )).click();
            System.out.println("Setting clicked successfully.....");
        } catch (Exception e) {
            System.out.println("Settings click failed..... " + e.getMessage());
        }
          
        Thread.sleep(1000);
        System.out.println("Application state reset");
    }
    
    private String sendInvitation() throws InterruptedException {
        System.out.println("Sending invitation...");
        
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/settings/account/information')]")
            )).click();
        
        Thread.sleep(1000);
        
        WebElement organization = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[text()='Organization']")
        ));
        organization.click();
        Thread.sleep(500);
        
        WebElement operatorTeam = null;
        for (int retry = 0; retry < 3; retry++) {
            try {
                operatorTeam = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//p[contains(text(), 'Operator')]")
                ));
                break;
            } catch (Exception e) {
                System.out.println("Retry " + (retry + 1) + " for Operator & Team...");
                Thread.sleep(2000);
                driver.navigate().refresh();
                Thread.sleep(2000);
            }
        }
        
        if (operatorTeam == null) {
            System.out.println("ERROR: Could not find Operator & Team");
            return null;
        }
        
        operatorTeam.click();
        Thread.sleep(500);
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Invitation']"))).click();
        Thread.sleep(300);
        
        WebElement invitationBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[.//section[text()='Invitation']]")
        ));
        invitationBtn.click();
        
        String tempEmail = currentEmailAutomate.getEmailAddress();
        System.out.println("Email: " + tempEmail);
        
        WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
            "//input[@placeholder='Enter the email address of the person you want to invite']"
        )));
        emailField.clear();
        emailField.sendKeys(tempEmail);
        Thread.sleep(100);
        
        Select roleSelect = new Select(driver.findElement(By.xpath("//select[@name='role_ids']")));
        roleSelect.selectByVisibleText("Admin");
        
        selectTeam();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section[text()='Invite Team Member']"))).click();
        System.out.println("Invitation sent successfully");
        return tempEmail;
    }
    
    private void selectTeam() throws InterruptedException {
        try {
            WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@id='react-select-2-input']")
            ));
            
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", input);
            Thread.sleep(500);
            js.executeScript("arguments[0].click();", input);
            Thread.sleep(500);
            
            input.sendKeys(Keys.ARROW_DOWN);
            Thread.sleep(1500);
            
            List<WebElement> teams = driver.findElements(By.xpath(
                "//div[contains(@class, 'option')] | //div[@role='option']"
            ));
            
            if (!teams.isEmpty()) {
                js.executeScript("arguments[0].click();", teams.get(0));
                System.out.println("Team selected: " + teams.get(0).getText());
            }
        } catch (Exception e) {
            System.out.println("Team selection skipped: " + e.getMessage());
        }
    }
    
    private boolean acceptInvitation() throws InterruptedException {
        System.out.println("Waiting for invitation email...");
        Thread.sleep(2000);
        
        String emailContent = currentEmailAutomate.waitForAndOpenNewEmail(45);
        
        if (emailContent == null) return false;
        
        System.out.println("Email received");
        
        currentEmailAutomate.clickAcceptByVisibleText();
        Thread.sleep(2000);
        
        currentEmailAutomate.clickAcceptInvite();
        Thread.sleep(2000);
        
        currentEmailAutomate.formFill();
        System.out.println("Account created successfully");
        
        return true;
    }
}