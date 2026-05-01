package pipelinestater;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.Random;

public class plCreateClients {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;
    
    // Constructor
    public CreateClients(WebDriver driver, WebDriverWait wait, JavascriptExecutor js) {
        this.driver = driver;
        this.wait = wait;
        this.js = (JavascriptExecutor) driver;
    }
    
    public void clickclientoption() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[.//p[contains(text(),'Clients')]]")
            )).click();
            System.out.println("\nClient click bhayo....");
        } catch (Exception e) {
            System.out.println("\nClient click bhayena " + e.getMessage());
        }
    }  
  
    public void clickNewEntry() {
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//section[text()='New Entry']")
        )).click();
    }
    
    public void Fullname() {
        Random random = new Random();
        
        String[] firstNames = {
            "Unique", "Sonu", "Kyurosh", "Parinita", "Hello", "Demo sathi", "Vikram", "Pooja",
            "Demo kumar", "Try kumar", "kcha", "Thikha", "Thikchaina", "Helloagain", "Hellothree", "Hellotwo"
        };
        
        String[] lastNames = {
            "Dai", "Hoho", "Demo", "Demosathi", "Hello", "Hi", "Bye",
            "How", "When", "Because"
        };
        
        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];
        String fullName = firstName + " " + lastName;
        
        wait.until(ExpectedConditions.elementToBeClickable(By.id("name"))).sendKeys(fullName);
        System.out.println("Generated Name: " + fullName);
    }
    
    public void Email() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String dynamicEmail = "demo" + timestamp + "@gds.com";
        wait.until(ExpectedConditions.elementToBeClickable(By.id("email"))).sendKeys(dynamicEmail);
        System.out.println("Created email: " + dynamicEmail);
    }
    
    public void phnum() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("phone"))).sendKeys("9800033307");
    }
    
    public void selectPlatform() throws InterruptedException {
        try {
            WebElement platform = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@data-state='closed' and contains(@class, 'group')]")
            ));
            js.executeScript("arguments[0].click();", platform);
            Thread.sleep(500);
            
            WebElement firstOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@role='option'] | //li[@role='option']")
            ));
            js.executeScript("arguments[0].click();", firstOption);
        } catch (Exception e) {
            System.out.println("Platform select failed: " + e.getMessage());
        }
    }
    
    public void selectCountry() throws InterruptedException {
        try {
            WebElement drop = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@data-slot='select-trigger' and .//span[@data-slot='select-value' and text()='Select Country']]")
            ));
            drop.sendKeys("Nepal" + Keys.ENTER);
            Actions actions = new Actions(driver);
            actions.sendKeys(Keys.ESCAPE).perform();
        } catch(Exception e) {
            System.out.println("Country selection failed");
        }
    }
 
    public void enterLocation(String location) throws InterruptedException {
        WebElement locationField = wait.until(ExpectedConditions.elementToBeClickable(By.id("location")));
        if (locationField.isEnabled()) {
            locationField.click();
            locationField.clear();
            locationField.sendKeys(location);
        } else {
            throw new RuntimeException("Location field is disabled");
        }
        driver.findElement(By.tagName("body")).click();
    }
    
    public void Profilelink() {
        WebElement profileurl = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section[text()='Add Profile Link']")));
        profileurl.click();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.id("social_links.0.link"))).sendKeys("https://instagram.com");
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@data-slot='select-trigger' and .//span[@data-slot='select-value' and contains(text(), 'Source')]]"))).click();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[span[text()='Instagram']]"))).click();
    }
    
    public void InternalNotes() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.name("note"))).sendKeys("Demo note");
            System.out.println("Internal notes click le bhayo...");
        } catch(Exception e1) {
            System.out.println("Internal notes click bhayo....");    
        }
    }
    
    public void createnotebtn() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//section[text()='Create Client']"))).click();
    }
}