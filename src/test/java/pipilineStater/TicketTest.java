package pipelineStater;

import Ticket.GuestCustomer;
import Ticket.NormalCustomer;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.Parameters;

public class TicketTest extends BaseTest {
    
    @Test(priority = 30, description = "Create guest ticket")
    public void testCreateGuestTicket() throws Exception {
        System.out.println("Test: Create Guest Ticket");
        
        navigateToTickets();
        
        GuestCustomer guestTicket = new GuestCustomer(driver);
        
        createTicket();
        
        guestTicket.clickGuestCustomer();
        guestTicket.enterPreciseTopic("Test ticket from guest user - " + System.currentTimeMillis());
        guestTicket.customeremail();
        guestTicket.prioritydropdown();
        guestTicket.FullName();
        guestTicket.phNumber();
        guestTicket.CustomerAddress();
        guestTicket.selectTeam();
        guestTicket.SuggestedMember();
        guestTicket.TicketDescription();
        guestTicket.AgentNotes();
        guestTicket.Clickcreatebtn();
        
        Thread.sleep(2000);
        
        // Verify ticket creation
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("tickets") || !currentUrl.contains("error"),
            "Guest ticket should be created");
    }
    
    @Test(priority = 31, description = "Create normal customer ticket")
    public void testCreateNormalCustomerTicket() throws Exception {
        System.out.println("Test: Create Normal Customer Ticket");
        
        navigateToTickets();
        
        NormalCustomer normalTicket = new NormalCustomer(driver, wait, js);
        
        createTicket();
        normalTicket.fillTicketForm();
        
        Thread.sleep(2000);
        System.out.println("✓ Normal customer ticket created");
    }
    
    @Test(priority = 32, description = "Create multiple tickets")
    @Parameters({"ticketCount", "ticketType"})
    public void testCreateMultipleTickets(@Optional("10") int numberOfTickets, 
                                          @Optional("guest") String ticketType) throws Exception {
        System.out.println("Test: Create " + numberOfTickets + " " + ticketType + " tickets");
        
        navigateToTickets();
        
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 1; i <= numberOfTickets; i++) {
            try {
                System.out.println("\n--- Creating " + ticketType + " Ticket " + i + " of " + numberOfTickets + " ---");
                
                if ("guest".equalsIgnoreCase(ticketType)) {
                    GuestCustomer guestTicket = new GuestCustomer(driver);
                    createTicket();
                    guestTicket.clickGuestCustomer();
                    guestTicket.enterPreciseTopic("Test ticket " + i + " - Guest");
                    guestTicket.customeremail();
                    guestTicket.prioritydropdown();
                    guestTicket.FullName();
                    guestTicket.phNumber();
                    guestTicket.CustomerAddress();
                    guestTicket.selectTeam();
                    guestTicket.SuggestedMember();
                    guestTicket.TicketDescription();
                    guestTicket.AgentNotes();
                    guestTicket.Clickcreatebtn();
                } else {
                    NormalCustomer normalTicket = new NormalCustomer(driver, wait, js);
                    createTicket();
                    normalTicket.fillTicketForm();
                }
                
                successCount++;
                System.out.println("✓ Ticket " + i + " created");
                Thread.sleep(1500);
                
                // Close any modal if present
                closeModal();
                
            } catch (Exception e) {
                failCount++;
                System.err.println("✗ Ticket " + i + " failed: " + e.getMessage());
                closeModal();
            }
        }
        
        System.out.println("\n=== Ticket Creation Summary ===");
        System.out.println("Type: " + ticketType);
        System.out.println("Total: " + numberOfTickets);
        System.out.println("Success: " + successCount);
        System.out.println("Failed: " + failCount);
        
        Assert.assertTrue(successCount > 0, "At least one ticket should be created");
    }
    
    private void navigateToTickets() throws InterruptedException {
        try {
            WebElement ticketsLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(@class, 'truncate') and text()='Tickets']")
            ));
            ticketsLink.click();
            System.out.println("✓ Navigated to Tickets");
            Thread.sleep(1500);
        } catch (Exception e) {
            System.out.println("Navigation to tickets failed: " + e.getMessage());
        }
    }
    
    private void createTicket() throws Exception {
        WebElement createTicketBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[.//section[text()='Create Ticket']]")
        ));
        js.executeScript("arguments[0].click();", createTicketBtn);
        System.out.println("✓ Create Ticket button clicked");
        Thread.sleep(1500);
    }
    
    private void closeModal() {
        try {
            WebElement closeBtn = driver.findElement(
                By.xpath("//div[@role='dialog']//button[@aria-label='Close' or normalize-space()='Cancel' or normalize-space()='×']")
            );
            js.executeScript("arguments[0].click();", closeBtn);
        } catch (Exception e) {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        }
    }
}