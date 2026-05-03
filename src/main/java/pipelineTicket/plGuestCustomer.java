package pipelineTicket;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class plGuestCustomer {
    
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;
    
    // FIXED: Constructor name matches class name
    public plGuestCustomer(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.js = (JavascriptExecutor) driver;
    }
    
    public void clickGuestCustomer() throws InterruptedException {
        Thread.sleep(2000);
        
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@role='dialog']")
        ));
        
        WebElement guestOption = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("button[aria-controls*='guest_customer']")
        ));
        guestOption.click();
        System.out.println(" Guest Customer clicked");
    }
    
    public void enterPreciseTopic(String topic) {
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//input[@placeholder='Your Precise Topic']")
        )).sendKeys(topic);
    }
    
    public void customeremail() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("customer_email"))).sendKeys("demo@demoo.com");
        } catch (Exception e) {
            System.out.println("Customermail click bhayena" + e.getMessage());
        }
    }
    
    public void prioritydropdown() throws InterruptedException {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@data-state='closed' and .//span[text()='Select Priority']]"))).click();
        } catch(Exception e) {
            System.out.println("Priority click bhayena+ e.getMessage()");
            Thread.sleep(1000);
        }
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='option']//span[text()='High']"))).click();
    }
    
    public void FullName() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Customer Full Name Here']"))).sendKeys("Demo kumar");
        } catch(Exception e) {
            System.out.println("Fullname click bhayena+ e.getMessage()");
        }
    }
    
    public void phNumber() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Customer Phone Number']"))).sendKeys("9806877793");
        } catch(Exception e) {
            System.out.println("Phnumber pass bhayene" + e.getMessage());
        }
    }
    
    public void CustomerAddress() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.name("customer_location"))).sendKeys("Chabahil");
        } catch(Exception e) {
            System.out.println("Customer address click bhayena" + e.getMessage());
        }
    }
    
    public void selectTeam() {
        WebElement teamDropdown = null;
        
        try {
            teamDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@role='combobox' and @aria-autocomplete='list']")));
            teamDropdown.click();
        } catch(Exception e) {
            System.out.println("Strategy le nih bayena" + e.getMessage());
        }
        
        if (teamDropdown != null) {
            teamDropdown.sendKeys(Keys.ARROW_DOWN);
            teamDropdown.sendKeys(Keys.ENTER);
            System.out.println(" First option selected using keyboard");
        } else {
            System.out.println("Team dropdown not found");
        }
    }
    
    public void SuggestedMember() throws InterruptedException {
        try {
            WebElement suggestedmemberdropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(text(),'Select Suggested Member')]/ancestor::div[contains(@class,'control')]")
            ));
            js.executeScript("arguments[0].click();", suggestedmemberdropdown);
            System.out.println("Suggested Member dropdown clicked");
            Thread.sleep(1000);
            
            WebElement input = driver.findElement(
                By.xpath("//div[contains(text(),'Select Suggested Member')]/following-sibling::div//input")
            );
            input.sendKeys(Keys.ARROW_DOWN);
            input.sendKeys(Keys.ENTER);
            System.out.println("Suggested Member selected");
        } catch (Exception e) {
            System.out.println("SuggestedMember failed: " + e.getMessage());
        }
    }
    
    public void TicketDescription() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("description"))).sendKeys("Demo");
        } catch(Exception e1) {
            System.out.println("Bhayena strategy2 pani...."+ e1.getMessage());
        }
    }
    
    public void AgentNotes() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("notes"))).sendKeys("Demo");
    }
    
    public void Clickcreatebtn() throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section[text()='Create Guest Ticket']"))).click();
        Thread.sleep(2000);
    }
}