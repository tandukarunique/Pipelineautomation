package stater;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Loginpage {

    private static final String APP_ORIGIN    = "https://dev.chatboq.com";
    private static final String AUTH_FILE     = "auth.json";
    private static final String LOGIN_PATH    = "/login";
    private static final String ORG_STORE_KEY = "X-Org-Id";

    private final WebDriver driver;
    private final JavascriptExecutor js;
    private final ObjectMapper mapper = new ObjectMapper();
    private final File authFile = new File(AUTH_FILE);

    public Loginpage(WebDriver driver) {
        this.driver = driver;
        this.js     = (JavascriptExecutor) driver;
    }

    // ─────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────

    public boolean loadSavedAuth() {
        AuthState auth = readAuthFile();
        if (auth == null) {
            System.out.println("No saved auth found. Manual login required.");
            return false;
        }

        try {
            driver.get(APP_ORIGIN + "/select-organization");
            sleep(2000);

            injectCookies(auth);
            injectLocalStorage(auth);

            System.out.println("Loaded saved authentication.");
            return true;
        } catch (Exception e) {
            System.err.println("Failed to load auth: " + e.getMessage());
            return false;
        }
    }

    public boolean manualLoginWithCaptcha(String email, String password) {
        System.out.println("Starting manual login. Solve CAPTCHA in the browser window.");

        driver.get(APP_ORIGIN + LOGIN_PATH);
        sleep(2000);

        try {
            fillCredentials(email, password);
        } catch (Exception e) {
            System.out.println("Could not auto-fill credentials, please fill them manually.");
        }

        System.out.println("==============================================");
        System.out.println("1. Solve the CAPTCHA in the browser");
        System.out.println("2. Click the Login button yourself");
        System.out.println("3. Wait until you see the dashboard/inbox in the browser");
        System.out.println("4. Then press ENTER here");
        System.out.println("==============================================");
        new Scanner(System.in).nextLine();

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL after ENTER: " + currentUrl);

        if (currentUrl.contains("/login")) {
            System.out.println("❌ Still on login page! Please complete login in the browser first, then press ENTER.");
            System.out.println("Press ENTER again once you are on the dashboard...");
            new Scanner(System.in).nextLine();
            currentUrl = driver.getCurrentUrl();
            System.out.println("Current URL: " + currentUrl);
        }

        if (currentUrl.contains("/login")) {
            System.err.println("❌ Login did not complete. auth.json NOT saved.");
            return false;
        }

        sleep(3000);

        saveAuthState();
        System.out.println("✅ Authentication saved to auth.json.");
        System.out.println("   Next time you run, login will be automatic!");
        return true;
    }

    public void gotoAuthenticated(String pathname, String expectedUrlPattern) {
        String url = resolveAppPath(pathname);
        System.out.println("Navigating to: " + url);
        driver.get(url);
        sleep(2000);

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Landed on: " + currentUrl);

        if (currentUrl.contains("/login")) {
            throw new RuntimeException(
                "Saved auth is no longer valid. Delete auth.json and run again to re-login."
            );
        }

        if (expectedUrlPattern != null) {
            try {
                new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.urlMatches(expectedUrlPattern));
            } catch (Exception e) {
                System.out.println("Warning: URL pattern not matched but not on login page. Continuing...");
            }
        }
    }

    public String getOrgId() {
        String orgId = getStoredValue(ORG_STORE_KEY);
        if (orgId == null) orgId = getStoredValue("organization_id");

        if (orgId == null) {
            AuthState auth = readAuthFile();
            if (auth != null && auth.cookies != null) {
                for (AuthState.CookieData c : auth.cookies) {
                    if ("organization".equals(c.name)) {
                        return c.value;
                    }
                }
            }
        }
        return orgId;
    }

    // ─────────────────────────────────────────────
    // INTERNAL HELPERS
    // ─────────────────────────────────────────────

    private String resolveAppPath(String pathname) {
        if (pathname == null || pathname.matches("^https?://.*")) return pathname;

        String normalized = pathname.startsWith("/") ? pathname : "/" + pathname;
        String orgId = getOrgId();

        if (orgId == null || normalized.startsWith("/" + orgId + "/")) {
            return APP_ORIGIN + normalized;
        }
        return APP_ORIGIN + "/" + orgId + normalized;
    }

    private void fillCredentials(String email, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("input[type='email'], input[name='email'], input[placeholder*='email' i]")
        ));
        emailField.clear();
        emailField.sendKeys(email);

        WebElement passwordField = driver.findElement(
            By.cssSelector("input[type='password'], input[name='password']")
        );
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    private void saveAuthState() {
        AuthState auth = new AuthState();

        auth.cookies = new ArrayList<>();
        for (Cookie c : driver.manage().getCookies()) {
            AuthState.CookieData cd = new AuthState.CookieData();
            cd.name     = c.getName();
            cd.value    = c.getValue();
            cd.domain   = c.getDomain();
            cd.path     = c.getPath();
            cd.expires  = c.getExpiry() != null ? (double) c.getExpiry().getTime() / 1000 : -1;
            cd.httpOnly = c.isHttpOnly();
            cd.secure   = c.isSecure();
            cd.sameSite = c.getSameSite();
            auth.cookies.add(cd);
        }
        System.out.println("Saved " + auth.cookies.size() + " cookies.");

        AuthState.OriginData origin = new AuthState.OriginData();
        origin.origin       = APP_ORIGIN;
        origin.localStorage = new ArrayList<>();

        try {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> ls =
                (java.util.Map<String, Object>) js.executeScript(
                    "var items = {};" +
                    "for (var i = 0; i < localStorage.length; i++) {" +
                    "  var k = localStorage.key(i);" +
                    "  items[k] = localStorage.getItem(k);" +
                    "}" +
                    "return items;"
                );

            if (ls != null) {
                ls.forEach((k, v) -> {
                    AuthState.LocalStorageItem item = new AuthState.LocalStorageItem();
                    item.name  = k;
                    item.value = String.valueOf(v);
                    origin.localStorage.add(item);
                });
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not read localStorage: " + e.getMessage());
        }

        System.out.println("Saved " + origin.localStorage.size() + " localStorage items.");
        auth.origins = List.of(origin);
        writeAuthFile(auth);
    }

    private void injectCookies(AuthState auth) {
        if (auth.cookies == null) return;
        int count = 0;
        for (AuthState.CookieData c : auth.cookies) {
            if (c.domain == null || !c.domain.contains("chatboq.com")) continue;
            try {
                Cookie.Builder builder = new Cookie.Builder(c.name, c.value)
                    .domain(c.domain)
                    .path(c.path != null ? c.path : "/")
                    .isSecure(c.secure)
                    .isHttpOnly(c.httpOnly);
                driver.manage().addCookie(builder.build());
                count++;
            } catch (Exception e) {
                System.err.println("Skipped cookie " + c.name + ": " + e.getMessage());
            }
        }
        System.out.println("Injected " + count + " cookies.");
    }

    private void injectLocalStorage(AuthState auth) {
        if (auth.origins == null) return;
        int count = 0;
        for (AuthState.OriginData origin : auth.origins) {
            if (!APP_ORIGIN.equals(origin.origin)) continue;
            if (origin.localStorage == null) continue;
            for (AuthState.LocalStorageItem item : origin.localStorage) {
                try {
                    js.executeScript(
                        "localStorage.setItem(arguments[0], arguments[1]);",
                        item.name, item.value
                    );
                    count++;
                } catch (Exception e) {
                    System.err.println("Skipped localStorage " + item.name + ": " + e.getMessage());
                }
            }
        }
        System.out.println("Injected " + count + " localStorage items.");
    }

    private String getStoredValue(String key) {
        try {
            Object val = js.executeScript("return localStorage.getItem(arguments[0]);", key);
            return val != null ? val.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private AuthState readAuthFile() {
        try {
            if (!authFile.exists()) return null;
            return mapper.readValue(authFile, AuthState.class);
        } catch (Exception e) {
            System.err.println("Could not read auth.json: " + e.getMessage());
            return null;
        }
    }

    private void writeAuthFile(AuthState auth) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(authFile, auth);
            System.out.println("auth.json written to: " + authFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Could not write auth.json: " + e.getMessage());
        }
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}