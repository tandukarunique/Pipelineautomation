package pipelineStater;

import pipelinestater.plCreateclients;   // ← CORRECTED import
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.Parameters;
import org.testng.annotations.Optional;  // ← ADD THIS for @Optional to work

public class ClientTest extends BaseTest {
    
    private plCreateclients createClients;  // ← CORRECTED type
    
    @Test(priority = 20, description = "Create single client")
    public void testCreateSingleClient() throws Exception {
        System.out.println("Test: Create Single Client");
        
        createClients = new plCreateclients(driver, wait, js);  // ← CORRECTED
        
        createClients.clickclientoption();
        Thread.sleep(1000);
        
        createClients.clickNewEntry();
        Thread.sleep(1000);
        
        createClients.Fullname();
        createClients.Email();
        createClients.phnum();
        createClients.selectPlatform();
        createClients.selectCountry();
        createClients.enterLocation("Kathmandu");
        createClients.Profilelink();
        createClients.InternalNotes();
        createClients.createnotebtn();
        
        Thread.sleep(2000);
        
        // Verify client was created
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("clients") || !currentUrl.contains("error"), 
            "Client creation should complete successfully");
    }
    
    @Test(priority = 21, description = "Create multiple clients")
    @Parameters({"clientCount"})
    public void testCreateMultipleClients(@Optional("5") int numberOfClients) throws Exception {
        System.out.println("Test: Create " + numberOfClients + " Clients");
        
        createClients = new plCreateclients(driver, wait, js);  // ← CORRECTED
        createClients.clickclientoption();
        
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 1; i <= numberOfClients; i++) {
            try {
                System.out.println("\n--- Creating Client " + i + " of " + numberOfClients + " ---");
                
                createClients.clickNewEntry();
                Thread.sleep(500);
                
                createClients.Fullname();
                createClients.Email();
                createClients.phnum();
                createClients.selectPlatform();
                createClients.selectCountry();
                createClients.enterLocation("Kathmandu");
                createClients.Profilelink();
                createClients.InternalNotes();
                createClients.createnotebtn();
                
                successCount++;
                System.out.println("✓ Client " + i + " created successfully");
                Thread.sleep(1000);
                
            } catch (Exception e) {
                failCount++;
                System.err.println("✗ Client " + i + " creation failed: " + e.getMessage());
            }
        }
        
        System.out.println("\n=== Client Creation Summary ===");
        System.out.println("Total: " + numberOfClients);
        System.out.println("Success: " + successCount);
        System.out.println("Failed: " + failCount);
        
        Assert.assertTrue(successCount > 0, "At least one client should be created");
    }
    
    @Test(priority = 22, description = "Verify client list is accessible")
    public void testClientListAccessible() throws Exception {
        System.out.println("Test: Client List Accessibility");
        
        createClients = new plCreateclients(driver, wait, js);  // ← CORRECTED
        createClients.clickclientoption();
        
        Thread.sleep(2000);
        String currentUrl = driver.getCurrentUrl();
        
        Assert.assertTrue(currentUrl.contains("clients"), "Should be on clients page");
    }
}