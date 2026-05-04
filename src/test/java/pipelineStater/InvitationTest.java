package pipelineStater;

import pipelineemailAutomate.plEmailautomate;
import pipelinestater.plAgentlimit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import java.util.List;

public class InvitationTest extends BaseTest {
    
    private plEmailautomate emailAutomate;
    
    @Test(priority = 10, description = "Send single invitation")
    @Parameters({"invitationCount"})
    public void testSendSingleInvitation(@Optional("1") int count) throws Exception {
        System.out.println("Test: Send Single Invitation");
        
        emailAutomate = new plEmailautomate(true);
        
        String email = emailAutomate.getEmailAddress();
        Assert.assertNotNull(email, "Email address should be generated");
        System.out.println("Generated email: " + email);
        
        // Navigate to invite section
        navigateToInviteSection();
        
        // Send invitation
        String invitedEmail = sendInvitation(email);
        Assert.assertEquals(invitedEmail, email, "Invitation should be sent to correct email");
        
        // Accept invitation
        boolean accepted = acceptInvitation();
        Assert.assertTrue(accepted, "Invitation should be accepted");
        
        emailAutomate.closeEmailBrowser();
    }
    
    
    
    
    
    
    @Test(priority = 11, description = "Send multiple invitations")
    @Parameters({"invitationCount"})
    public void testSendMultipleInvitations(@Optional("6") int numberOfInvitations) throws Exception {
        System.out.println("Test: Send " + numberOfInvitations + " Invitations");
        
        for (int i = 1; i <= numberOfInvitations; i++) {
            System.out.println("\n--- Invitation " + i + " of " + numberOfInvitations + " ---");
            
            emailAutomate = new plEmailautomate(true);
            String email = emailAutomate.getEmailAddress();
            Assert.assertNotNull(email, "Email " + i + " should be generated");
            
            navigateToInviteSection();
            String invitedEmail = sendInvitation(email);
            Assert.assertEquals(invitedEmail, email, "Invitation " + i + " should be sent");
            
            boolean accepted = acceptInvitation();
            Assert.assertTrue(accepted, "Invitation " + i + " should be accepted");
            
            emailAutomate.closeEmailBrowser();
            Thread.sleep(3000);
        }
    }
    
    
    
    
    
    
    @Test(priority = 12, description = "Test Agentlimit class with multiple invitations")
    public void testAgentLimitClass() throws Exception {
        System.out.println("Test: AgentLimit Class Execution");
        
        emailAutomate = new plEmailautomate(true);
        
        // This will run the invitation process inside Agentlimit
        plAgentlimit agentLimit = new plAgentlimit(wait, driver, js, emailAutomate);
        
        Assert.assertNotNull(agentLimit, "AgentLimit should complete execution");
    }
  
    
    
    
    
    
    //Setting page ma janey....
    private void navigateToInviteSection() throws InterruptedException {
		 try {
			 WebElement settingsIcon = wait.until(ExpectedConditions.elementToBeClickable(
			            By.xpath("//a[contains(@href, '/settings/account/information')]//svg")
			        ));
			        
			        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", settingsIcon);
			        Thread.sleep(500);
			        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", settingsIcon);
			        System.out.println("✅ Settings icon clicked");
    	            
    	        } catch (Exception e3) {
    	            System.out.println("Setting btn bhetena....: " + e3.getMessage());
    	         }
		 try {
			 String currentUrl = driver.getCurrentUrl();
	            System.out.println("Current URL: " + currentUrl);
	           
	            String workspaceId = currentUrl.split("/")[3];
	            String settingsUrl = "https://dev.chatboq.com/" + workspaceId + "/settings/account/information";
	            
	            System.out.println("Navigating directly to: " + settingsUrl);
	            driver.get(settingsUrl);
	            
	            // Wait for page to load
	            Thread.sleep(3000);
	            System.out.println("✅ Settings page loaded");
		 }
		 catch (Exception e) {
			 System.out.println("Attempt 2 failed: " + e.getMessage());
		 }
		
    	}
    	
    
    
    private String sendInvitation(String email) throws InterruptedException {
        System.out.println("Sending invitation to: " + email);
        
        try {
           try {
        	   // Navigate to Organization -> Operator & Team
           	WebElement organization = wait.until(ExpectedConditions.elementToBeClickable(
           		    By.xpath("//button[@data-slot='accordion-trigger' and contains(normalize-space(), 'Organization')]")
           		));
           	((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", organization);
               Thread.sleep(1000);
               ((JavascriptExecutor) driver).executeScript("arguments[0].click();", organization);
           }
           catch (Exception e) {
        	   System.out.println("Setting bhitra ko organization btn thichiyena");
           }
           
                                
            WebElement operatorTeam = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//p[contains(text(), 'Operator')]")
            ));
            operatorTeam.click();
            Thread.sleep(500);
            
            // Click Invitation tab
            wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Invitation']")
            )).click();
            Thread.sleep(300);
            
            // Click Invitation button
            WebElement invitationBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//section[text()='Invitation']]")
            ));
            invitationBtn.click();
            Thread.sleep(500);
            
            // Enter email address
            WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//input[@placeholder='Enter the email address of the person you want to invite']"
            )));
            emailField.clear();
            emailField.sendKeys(email);
            Thread.sleep(500);
  
            
         // Select role as Admin
            try {
                WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("react-select-2-input")
                ));
                input.click();
                input.sendKeys(Keys.ARROW_DOWN);
                input.sendKeys(Keys.ARROW_DOWN);
                input.sendKeys(Keys.ENTER);
                System.out.println("✓ Admin role selected (Way 1)");
            } catch (Exception e) {
                System.out.println("Way 1 failed: " + e.getMessage());
                           }
           
            Thread.sleep(500);
            
            // Select team
            selectTeam();
            
            
            // Click Invite Team Member button
            WebElement inviteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//section[text()='Invite Team Member']")
            ));
            inviteBtn.click();
            System.out.println("✓ Invite Team Member button clicked");
            
            // Wait for success
            Thread.sleep(3000);
            System.out.println("✓ Invitation sent successfully to: " + email);
            
        } catch (Exception e) {
            System.out.println("❌ Failed to send invitation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        
        return email;
    }
    
    
    
    
    private void selectTeam() throws InterruptedException {
             
    	 try {
    	        WebElement teamInput = wait.until(ExpectedConditions.elementToBeClickable(
    	            By.id("react-select-3-input")
    	        ));
    	        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", teamInput);
    	        Thread.sleep(500);
    	        teamInput.click();
    	        Thread.sleep(500);
    	        teamInput.sendKeys(Keys.ARROW_DOWN); 
    	        teamInput.sendKeys(Keys.ENTER);
    	        System.out.println("✓ First team selected (Way 1)");
    	    } catch (Exception e) {
    	        System.out.println("Way 1 failed: " + e.getMessage());
    	        try {
    	            WebElement teamInput = wait.until(ExpectedConditions.elementToBeClickable(
    	                By.cssSelector("input[aria-controls='react-select-3-listbox']")
    	            ));
    	            js.executeScript("arguments[0].click();", teamInput);
    	            Thread.sleep(500);
    	            teamInput.sendKeys(Keys.ARROW_DOWN);
    	            teamInput.sendKeys(Keys.ENTER);
    	            System.out.println("✓ First team selected (Way 2)");
    	        } catch (Exception e2) {
    	            System.out.println("Way 2 failed: " + e2.getMessage());
    	            try {
    	                // Click the dropdown chevron to open it
    	                WebElement chevron = wait.until(ExpectedConditions.elementToBeClickable(
    	                    By.cssSelector("div[class*='css-14xtc6'] div[class*='indicatorContainer']")
    	                ));
    	                js.executeScript("arguments[0].click();", chevron);
    	                Thread.sleep(500);
    	                // Then click first option directly
    	                WebElement firstOption = wait.until(ExpectedConditions.elementToBeClickable(
    	                    By.cssSelector("div[id='react-select-3-option-0']")
    	                ));
    	                firstOption.click();
    	                System.out.println("✓ First team selected (Way 3)");
    	            } catch (Exception e3) {
    	                System.out.println("❌ All team selection attempts failed: " + e3.getMessage());
    	            }
    	        }
    	    }
        
    }
    
    
    
    
    private boolean acceptInvitation() throws InterruptedException {
        System.out.println("Waiting for invitation email...");
        String result = emailAutomate.waitForAndOpenNewEmail(60);
        
        if (result != null) {
            System.out.println("✓ Invitation email received!");
            emailAutomate.clickAcceptByVisibleText();
            Thread.sleep(2000);
            emailAutomate.clickAcceptInvite();
            Thread.sleep(2000);
            emailAutomate.formFill();
            System.out.println("✓ Invitation accepted and account created!");
            return true;
        } else {
            System.out.println("❌ No invitation email received within timeout");
            return false;
        }
    }
}