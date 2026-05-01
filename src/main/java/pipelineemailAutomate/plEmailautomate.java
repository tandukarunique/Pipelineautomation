package pipelineemailAutomate;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Set;

public class plEmailautomate {

    private WebDriver emailDriver;
    private String emailAddress;
    private WebDriverWait wait;
    private String mainWindowHandle;
    private boolean isIncognito;

    // ✅ FIXED constructor name
    public plEmailautomate() {
        this(false);
    }

    public plEmailautomate(boolean incognitoMode) {
        this.isIncognito = incognitoMode;

        ChromeOptions options = new ChromeOptions();

        if (incognitoMode) {
            options.addArguments("--incognito");
            System.out.println("🔒 Opening Chrome in INCOGNITO mode for new email");
        }

        options.addArguments("--window-position=1000,0");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        this.emailDriver = new ChromeDriver(options);
        this.emailDriver.manage().window().maximize();
        this.wait = new WebDriverWait(emailDriver, Duration.ofSeconds(30));

        generateTempEmail();
    }

    private void generateTempEmail() {
        try {
            emailDriver.get("https://www.guerrillamail.com/");
            Thread.sleep(4000);

            mainWindowHandle = emailDriver.getWindowHandle();

            WebElement useAliasBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("use-alias"))
            );

            JavascriptExecutor js = (JavascriptExecutor) emailDriver;

            js.executeScript("arguments[0].click();", useAliasBtn);

            Thread.sleep(2000);

            this.emailAddress = (String) js.executeScript(
                "return document.getElementById('email-widget').innerText.trim();"
            );

            System.out.println("Generated temporary email: " + emailAddress);

        } catch (Exception e) {
            System.out.println("Error generating email: " + e.getMessage());
        }
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void clickAcceptInvite() {
        try {
            System.out.println("Looking for 'Accept Invite' button...");
            Thread.sleep(2000);

            WebElement acceptBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[contains(text(), 'Accept Invite')]")
                )
            );

            JavascriptExecutor js = (JavascriptExecutor) emailDriver;

            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", acceptBtn);
            Thread.sleep(500);

            js.executeScript("arguments[0].click();", acceptBtn);

            System.out.println("✅ Clicked Accept Invite button");

        } catch (Exception e) {
            System.out.println("❌ Failed to click Accept Invite: " + e.getMessage());
        }
    }
}