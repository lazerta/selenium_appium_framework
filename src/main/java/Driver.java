import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import lombok.Data;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Driver {
    private static Driver instance = null;
    private String browserHandle = null;
    private static final int IMPLICIT_TIMEOUT = 0;
    private ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();
    private ThreadLocal<AppiumDriver<MobileElement>> mobileDriver = new ThreadLocal<>();
    private ThreadLocal<String> sessionId = new ThreadLocal<>();
    private ThreadLocal<String> sessionBrowser = new ThreadLocal<>();
    private ThreadLocal<String> sessionPlatform =
            new ThreadLocal<>();

    private ThreadLocal<String> sessionVersion =
            new ThreadLocal<>();

    private String getEnv = null;


    public static Driver getInstance() {
        if (instance == null){
            instance = new Driver();

        }
        return instance;
    }

    /**
     *
     * @param browser Chrome, Firefox, Internet Explorer, Microsoft Edge, Opera, Safari (iPhone/iPad, or Android for mobile)
     * @param   environment, remote, and Sauce Labs
     * @param platform Linux, Windows, Mac, Sierra, Win10 (iPhone/iPad, or Android for mobile)
     * @param optPreferences
     * @throws Exception
     */
    @SafeVarargs
    public final void setDriver(String browser,
                                 String environment,
                                String platform,
                                Map<String, Object>... optPreferences)
            throws Exception {

        DesiredCapabilities caps = null;
        String localHub = "http://127.0.0.1:4723/wd/hub";
        String getPlatform = null;
        browser = browser.toLowerCase();

        switch (browser) {
            case "firefox":
                caps = DesiredCapabilities.firefox();
                caps.setCapability("marionette", true);
                FirefoxProfile profile = new FirefoxProfile();
                // incognito for firefox
                profile.setPreference("browser.privatebrowsing.autostart", true);
                caps.setCapability(FirefoxDriver.PROFILE, profile);
               if (environment.equalsIgnoreCase("local")){
                   resolveLocalDriver(platform);
               }
                webDriver.set(new FirefoxDriver(caps));



                break;
            case "chrome":
                caps = DesiredCapabilities.chrome();
                ChromeOptions options = new ChromeOptions();
                Map<String, Object> chromePrefs = new HashMap<String, Object>();
                webDriver.set(new ChromeDriver(caps));

                break;
            case "ie":
                caps = DesiredCapabilities.internetExplorer();
                webDriver.set(new
                        InternetExplorerDriver(caps));

                break;
            case "safari":
                caps = DesiredCapabilities.safari();
                webDriver.set(new SafariDriver(caps));

                break;
            case "edge":
                caps = DesiredCapabilities.edge();
                webDriver.set(new EdgeDriver(caps));

                break;
            case "iphone":
            case "ipad":
                if (browser.equalsIgnoreCase("ipad")) {
                    caps = DesiredCapabilities.ipad();
                } else {
                    caps = DesiredCapabilities.iphone();
                }

                mobileDriver.set(new IOSDriver<>(
                        new URL(localHub), caps));

                break;
            case "android":
                caps = DesiredCapabilities.android();
                mobileDriver.set(new
                        AndroidDriver<>(
                        new URL(localHub), caps));

                break;
        }
    }

    private void resolveLocalDriver(String platform) {
    }


    public void setDriver(WebDriver driver) {
        webDriver.set(driver);
        sessionId.set(((RemoteWebDriver) webDriver.get()).getSessionId().toString());
        sessionBrowser.set(((RemoteWebDriver) webDriver.get()).getCapabilities().getBrowserName());
        sessionPlatform.set(((RemoteWebDriver) webDriver.get()).getCapabilities().getPlatform().toString());
        setBrowserHandle(getDriver().getWindowHandle());
    }


    public void setDriver(AppiumDriver<MobileElement> driver) {
        mobileDriver.set(driver);
        sessionId.set(mobileDriver.get().getSessionId().toString());
        sessionBrowser.set(mobileDriver.get().getCapabilities().getBrowserName());
        sessionPlatform.set(mobileDriver.get().getCapabilities().getPlatform().toString());
    }

    public void setBrowserHandle(String browserHandle) {
        this.browserHandle = browserHandle;
    }

    public WebDriver getDriver() {
        return webDriver.get();
    }

    public AppiumDriver<MobileElement> getDriver(boolean isMobile) {
        return mobileDriver.get();
    }

    public String getSessionBrowser() {
        return sessionBrowser.get();
    }

    public WebDriver getCurrentDriver() {
        if (getInstance().getSessionBrowser().contains("iphone") || getInstance().getSessionBrowser().contains("ipad") || getInstance().getSessionBrowser().contains("android")) {
            return getInstance().getDriver(true);
        } else {
            return getInstance().getDriver();
        }
    }


}
