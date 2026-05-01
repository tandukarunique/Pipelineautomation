package pipelineStater;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MessageTest extends BaseTest {
    
    @Test(priority = 40, description = "Send message in chat")
    public void testSendMessage() throws Exception {
        System.out.println("Test: Send Message");
        
        navigateToChat();
        
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[@contenteditable='true']")
        ));
        input.click();
        
        String testMessage = "Test message - " + System.currentTimeMillis();
        input.sendKeys(testMessage);
        Thread.sleep(500);
        input.sendKeys(Keys.ENTER);
        
        System.out.println("✓ Message sent: " + testMessage);
        Thread.sleep(2000);
        
        // Verify message appears
        WebElement sentMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//p[contains(@class,'leading-7')]//span[contains(text(),'" + testMessage + "')]")
        ));
        Assert.assertNotNull(sentMessage, "Message should appear in chat");
    }
    
    @Test(priority = 41, description = "Edit message")
    public void testEditMessage() throws Exception {
        System.out.println("Test: Edit Message");
        
        navigateToChat();
        
        // Send a message first
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[@contenteditable='true']")
        ));
        input.click();
        input.sendKeys("Original message for editing");
        input.sendKeys(Keys.ENTER);
        Thread.sleep(2000);
        
        // Find and edit the message
        WebElement originalMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//p[contains(@class,'leading-7')]//span[contains(text(),'Original message')]")
        ));
        
        Actions actions = new Actions(driver);
        actions.moveToElement(originalMessage).perform();
        Thread.sleep(2000);
        
        // Find and click the edit trigger
        WebElement trigger = (WebElement) js.executeScript(
            "for(var s of document.querySelectorAll('p[class*=\"leading-7\"] span')){" +
            "  if(s.textContent.trim()==='Original message for editing'){" +
            "    var c=s.parentElement;" +
            "    while(c && c.tagName!=='BODY'){" +
            "      var t=c.querySelector('[data-slot=\"dropdown-menu-trigger\"]');" +
            "      if(t) return t;" +
            "      c=c.parentElement;" +
            "    }" +
            "  }" +
            "} return null;"
        );
        
        if (trigger != null) {
            actions.moveToElement(originalMessage)
                   .pause(800)
                   .moveToElement(trigger)
                   .pause(500)
                   .click()
                   .perform();
            Thread.sleep(2000);
            
            driver.findElement(By.xpath("//*[normalize-space(text())='Edit']")).click();
            Thread.sleep(1000);
            
            WebElement editInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@contenteditable='true']")
            ));
            editInput.click();
            editInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            editInput.sendKeys("EDITED: Message was modified");
            editInput.sendKeys(Keys.ENTER);
            
            System.out.println("✓ Message edited successfully");
        } else {
            Assert.fail("Could not find edit trigger");
        }
    }
    
    @Test(priority = 42, description = "Delete message")
    public void testDeleteMessage() throws Exception {
        System.out.println("Test: Delete Message");
        
        navigateToChat();
        
        // Send a message
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[@contenteditable='true']")
        ));
        input.click();
        input.sendKeys("Message to be deleted");
        input.sendKeys(Keys.ENTER);
        Thread.sleep(2000);
        
        // Delete the message
        WebElement messageToDelete = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//p[contains(@class,'leading-7')]//span[contains(text(),'Message to be deleted')]")
        ));
        
        Actions actions = new Actions(driver);
        actions.moveToElement(messageToDelete).perform();
        
        WebElement trigger = (WebElement) js.executeScript(
            "for(var s of document.querySelectorAll('p[class*=\"leading-7\"] span')){" +
            "  if(s.textContent.trim()==='Message to be deleted'){" +
            "    var c=s.parentElement;" +
            "    while(c && c.tagName!=='BODY'){" +
            "      var t=c.querySelector('[data-slot=\"dropdown-menu-trigger\"]');" +
            "      if(t) return t;" +
            "      c=c.parentElement;" +
            "    }" +
            "  }" +
            "} return null;"
        );
        
        if (trigger != null) {
            actions.moveToElement(messageToDelete)
                   .pause(800)
                   .moveToElement(trigger)
                   .pause(500)
                   .click()
                   .perform();
            Thread.sleep(2000);
            
            driver.findElement(By.xpath("//*[normalize-space(text())='Delete']")).click();
            Thread.sleep(1000);
            
            WebElement confirmDelete = driver.findElement(
                By.xpath("//div[contains(@class, 'fixed')]//*[text()='Delete']")
            );
            js.executeScript("arguments[0].click();", confirmDelete);
            
            System.out.println("✓ Message deleted successfully");
        } else {
            Assert.fail("Could not find delete trigger");
        }
    }
    
    private void navigateToChat() throws Exception {
        try {
            WebElement demoChat = driver.findElement(
                By.xpath("//button[contains(., 'Demo')]")
            );
            js.executeScript("arguments[0].click();", demoChat);
            System.out.println("✓ Demo chat selected");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Could not find Demo chat: " + e.getMessage());
            throw e;
        }
    }
}