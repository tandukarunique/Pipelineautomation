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
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

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

            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", useAliasBtn);
            Thread.sleep(500);
            js.executeScript("arguments[0].click();", useAliasBtn);
            System.out.println("✓ Clicked use-alias button!");

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

    private void ensureCorrectWindow() {
        try {
            Set<String> handles = emailDriver.getWindowHandles();
            if (handles.contains(mainWindowHandle)) {
                emailDriver.switchTo().window(mainWindowHandle);
            } else {
                for (String handle : handles) {
                    emailDriver.switchTo().window(handle);
                    String url = emailDriver.getCurrentUrl();
                    if (url.contains("guerrillamail") || url.contains("sharklasers")) {
                        mainWindowHandle = handle;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error ensuring correct window: " + e.getMessage());
        }
    }

    public String waitForAndOpenNewEmail(int timeoutSeconds) throws InterruptedException {
        System.out.println("Waiting for invitation email from noreply@chatboq.com...");
        System.out.println("Timeout: " + timeoutSeconds + " seconds");

        long startTime = System.currentTimeMillis();

        ensureCorrectWindow();
        System.out.println("Current email page URL: " + emailDriver.getCurrentUrl());

        JavascriptExecutor js = (JavascriptExecutor) emailDriver;

        while ((System.currentTimeMillis() - startTime) < (timeoutSeconds * 1000L)) {
            try {
                System.out.println("Checking inbox... elapsed: " +
                    ((System.currentTimeMillis() - startTime) / 1000) + "s");

                js.executeScript("if(typeof GRML !== 'undefined') { GRML.check_email(); }");
                System.out.println("Checking email...");
                Thread.sleep(2000);

                Object rowCountObj = js.executeScript(
                    "var tbody = document.getElementById('email_list');" +
                    "if (!tbody) return -1;" +
                    "return tbody.querySelectorAll('tr.mail_row').length;"
                );
                long rowCount = rowCountObj != null ? ((Number) rowCountObj).longValue() : -1;
                System.out.println("Mail rows in DOM: " + rowCount);

                if (rowCount > 0) {
                    String allSenders = (String) js.executeScript(
                        "var rows = document.querySelectorAll('#email_list tr.mail_row');" +
                        "var out = [];" +
                        "for (var i = 0; i < rows.length; i++) {" +
                        "  var td2 = rows[i].querySelector('td.td2');" +
                        "  if (td2) out.push('[' + td2.innerText.trim() + ']');" +
                        "}" +
                        "return out.join(', ');"
                    );
                    System.out.println("Senders: " + allSenders);

                    WebElement targetRow = (WebElement) js.executeScript(
                        "var rows = document.querySelectorAll('#email_list tr.mail_row');" +
                        "for (var i = 0; i < rows.length; i++) {" +
                        "  var td2 = rows[i].querySelector('td.td2');" +
                        "  if (td2 && td2.innerText.trim().toLowerCase() === 'noreply@chatboq.com') {" +
                        "    return rows[i];" +
                        "  }" +
                        "}" +
                        "return null;"
                    );

                    if (targetRow != null) {
                        System.out.println("✓ Found email from noreply@chatboq.com!");
                        js.executeScript("arguments[0].click();", targetRow);
                        Thread.sleep(3000);
                        return "EMAIL_OPENED";
                    }
                }

                System.out.println("Not found yet, waiting 5s...");
                Thread.sleep(5000);

            } catch (Exception e) {
                System.out.println("Error while checking inbox: " + e.getMessage());
                Thread.sleep(3000);
            }
        }

        System.out.println("⏰ Timeout reached - no email found");
        return null;
    }

    public void clickAcceptByVisibleText() {
        try {
            System.out.println("Looking for 'Accept Invitation' link in email...");

            Set<String> windowsBefore = emailDriver.getWindowHandles();
            System.out.println("Windows before click: " + windowsBefore.size());

            ensureCorrectWindow();
            Thread.sleep(1000);
            emailDriver.switchTo().defaultContent();

            JavascriptExecutor js = (JavascriptExecutor) emailDriver;
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(800);

            WebElement acceptLink = emailDriver.findElement(
                By.xpath("//a[contains(@href, 'accept-invitation')]")
            );
            System.out.println("✓ Found by href: accept-invitation");

            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", acceptLink);
            Thread.sleep(500);
            js.executeScript("arguments[0].click();", acceptLink);
            System.out.println("✓ Clicked Accept Invitation link!");

            Thread.sleep(2000);

            System.out.println("Waiting for new tab...");
            long waitStart = System.currentTimeMillis();
            while (System.currentTimeMillis() - waitStart < 10000) {
                Set<String> windowsAfter = emailDriver.getWindowHandles();
                if (windowsAfter.size() > windowsBefore.size()) {
                    for (String handle : windowsAfter) {
                        if (!windowsBefore.contains(handle)) {
                            emailDriver.switchTo().window(handle);
                            System.out.println("✓ Switched to new tab: " + emailDriver.getCurrentUrl());
                            break;
                        }
                    }
                    break;
                }
                Thread.sleep(500);
            }

        } catch (Exception e) {
            System.out.println("Failed to click Accept Invitation: " + e.getMessage());
        }
    }

    public void clickAcceptInvite() {
        try {
            System.out.println("Looking for 'Accept Invite' button...");
            Thread.sleep(2000);

            System.out.println("Current window URL: " + emailDriver.getCurrentUrl());

            WebElement acceptBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(text(), 'Accept Invite')]")
            ));
            JavascriptExecutor js = (JavascriptExecutor) emailDriver;
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", acceptBtn);
            Thread.sleep(500);
            js.executeScript("arguments[0].click();", acceptBtn);
            System.out.println("✓ Clicked 'Accept Invite'!");
            Thread.sleep(2000);

        } catch (Exception e) {
            System.out.println("Failed to click Accept Invite: " + e.getMessage());
        }
    }

    public void formFill() {
        try {
            System.out.println("Starting form fill...");
            Thread.sleep(2000);

            JavascriptExecutor js = (JavascriptExecutor) emailDriver;

            WebElement nameField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@placeholder='Enter Full Name']")
            ));
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", nameField);
            Thread.sleep(300);
            nameField.clear();
            nameField.sendKeys("Demo User");
            System.out.println("✓ Name entered");

            WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@placeholder='******']")
            ));
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", passwordField);
            Thread.sleep(300);
            passwordField.clear();
            passwordField.sendKeys("Password123!");
            System.out.println("✓ Password entered");

            WebElement confirmPasswordField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("confirm_password")
            ));
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", confirmPasswordField);
            Thread.sleep(300);
            confirmPasswordField.clear();
            confirmPasswordField.sendKeys("Password123!");
            System.out.println("✓ Confirm password entered");

            Thread.sleep(500);

            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(800);

            WebElement nextBtn = emailDriver.findElement(By.xpath(
                "//div[contains(@class,'flex')]//section[normalize-space(text())='Next']"
            ));
            System.out.println("✓ Found Next button");

            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", nextBtn);
            Thread.sleep(500);
            wait.until(ExpectedConditions.elementToBeClickable(nextBtn)).click();
            System.out.println("✓ Clicked Next button!");

            Thread.sleep(2000);
            System.out.println("✓ Form submission completed!");

        } catch (Exception e) {
            System.out.println("Form fill failed: " + e.getMessage());
        }
    }

    public void closeEmailBrowser() {
        if (emailDriver != null) {
            try {
                emailDriver.quit();
                System.out.println("✓ Email browser closed");
            } catch (Exception e) {
                System.out.println("Error closing email browser: " + e.getMessage());
            }
        }
    }
}