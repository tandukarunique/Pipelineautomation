package pipelinestater;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;

public class plFileUploadTest {

    public static void UploadMultipleFiles(WebDriver driver, WebDriverWait wait, JavascriptExecutor js, String[] files) {
        
        try {
            WebElement plusButton = driver.findElement(By.xpath("//button[.//*[local-name()='path' and contains(@d, 'M12 6C12 6.13261')]]"));
            js.executeScript("arguments[0].click();", plusButton);
            System.out.println("Clicked plus button");
            Thread.sleep(1500);
            
            WebElement documentOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Document']")
            ));
            documentOption.click();
            System.out.println("Clicked Document option");
            Thread.sleep(2000);
            
            StringBuilder multipleFiles = new StringBuilder();
            for (String filePath : files) {
                File uploadFile = new File(filePath);
                if (!uploadFile.exists()) {
                    System.out.println("File does not exist: " + filePath);
                    continue;
                }
                if (multipleFiles.length() > 0) {
                    multipleFiles.append(" ");
                }
                multipleFiles.append("\"").append(uploadFile.getAbsolutePath()).append("\"");
            }
            
            if (multipleFiles.length() == 0) {
                System.out.println("No valid files to upload");
                return;
            }
            
            System.out.println("Selecting files: " + multipleFiles.toString());
            
            StringSelection stringSelection = new StringSelection(multipleFiles.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            
            Robot robot = new Robot();
            Thread.sleep(1000);
            
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            System.out.println("Pasted file paths");
            
            Thread.sleep(1000);
            
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            System.out.println("Selected " + files.length + " files");
            Thread.sleep(3000);
            
            WebElement messageInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@role='textbox'] | //textarea")
            ));
            messageInput.sendKeys(Keys.ENTER);
            System.out.println("Message sent with " + files.length + " files");
            
        } catch (Exception e) {
            System.out.println("Upload failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}