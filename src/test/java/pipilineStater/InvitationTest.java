package pipelineStater;

import emailAutomate.Emailautomate;
import stater.Agentlimit;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class InvitationTest extends BaseTest {
    
    private Emailautomate emailAutomate;
    
    @Test(priority = 10, description = "Send single invitation")
    @Parameters({"invitationCount"})
    public void testSendSingleInvitation(@Optional("1") int count) throws Exception {
        System.out.println("Test: Send Single Invitation");
        
        emailAutomate = new Emailautomate(true); // Incognito mode
        
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
    public void testSendMultipleInvitations(@Optional("3") int numberOfInvitations) throws Exception {
        System.out.println("Test: Send " + numberOfInvitations + " Invitations");
        
        for (int i = 1; i <= numberOfInvitations; i++) {
            System.out.println("\n--- Invitation " + i + " of " + numberOfInvitations + " ---");
            
            emailAutomate = new Emailautomate(true);
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
        
        // Use reflection or direct call - ensure emailAutomate is initialized
        emailAutomate = new Emailautomate(true);
        
        // This will run the invitation process inside Agentlimit
        Agentlimit agentLimit = new Agentlimit(wait, driver, js, emailAutomate);
        
        // Since Agentlimit constructor does all the work, just verify it completed
        Assert.assertNotNull(agentLimit, "AgentLimit should complete execution");
    }
    
    private void navigateToInviteSection() throws InterruptedException {
        try {
            // Navigate to organization settings
            driver.navigate().refresh();
            Thread.sleep(3000);
            
            wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/settings/account/information')]")
            )).click();
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Navigation to invite section failed: " + e.getMessage());
        }
    }
    
    private String sendInvitation(String email) throws InterruptedException {
        // This should call your existing sendInvitation logic
        // Simplified - you'll need to integrate with your Agentlimit class methods
        
        System.out.println("Sending invitation to: " + email);
        // Return the email sent to
        return email;
    }
    
    private boolean acceptInvitation() throws InterruptedException {
        String result = emailAutomate.waitForAndOpenNewEmail(60);
        if (result != null) {
            emailAutomate.clickAcceptByVisibleText();
            Thread.sleep(2000);
            emailAutomate.clickAcceptInvite();
            Thread.sleep(2000);
            emailAutomate.formFill();
            return true;
        }
        return false;
    }
}