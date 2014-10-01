package com.android.tuto.test;

import io.appium.java_client.AppiumDriver;

import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.gson.JsonParser;

public class TurnOnBTTest {
    private AppiumDriver driver;
    private static final String url = "http://127.0.0.1:4723/wd/hub";
    private static final HttpClient client = HttpClients.createDefault();
    private static final JsonParser parser = new JsonParser();

    @Test
    public void apiDemo() throws Exception {
        // driver.scrollTo("about phone");

        // driver.scrollTo("bluetooth");
        // WebElement moreElement = driver.findElement(By.name("More"));
        // moreElement.click();
        // driver.scrollTo("About device");
        // List<WebElement> linearLayout = driver.findElementsByClassName("android.widget.LinearLayout");
        // linearLayout.get(1).click();
        WebElement bluetooth = driver.findElement(By.name("Bluetooth"));
        bluetooth.click();

        WebElement switchOnOff = driver.findElement(By.className("android.widget.Switch"));
        if (!switchOnOff.getText().equalsIgnoreCase("on")) {
            switchOnOff.click();
            //WebDriverWait wait = new WebDriverWait(driver, 10000);
            // wait.wait(10000);
            // wait.until(ExpectedConditions.elementToBeClickable(By.className("android.widget.TextView")));
            // new WebDriverWait(driver, 10);
            // WebElement myDynamicElement = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By
            // .className("android.widget.TextView")));
            waitForVisible(By.name("Only visible to paired devices. Tap to make visible to other devices."), 10);
        }
        WebElement checkboxEl = driver.findElement(By.className("android.widget.CheckBox"));
        if (checkboxEl.getAttribute("checked").equalsIgnoreCase("false")) {
            checkboxEl.click();
        }
        Thread.sleep(10000);
    }

    public void waitForVisible(final By by, int waitTime) {
        int timeoutInSeconds = 30;
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        for (int attempt = 0; attempt < waitTime; attempt++) {
            try {
                driver.findElement(by);
                break;
            } catch (Exception e) {
                driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            }
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    @Before
    public void setUp() throws Exception {
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.BROWSER_NAME, "");
        // capabilities.setCapability("deviceName", "Android Emulator");
        capabilities.setCapability("deviceName", "MNGO");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appPackage", "com.android.settings");
        capabilities.setCapability("appActivity", ".Settings");
        // for real device
        capabilities.setCapability("version", "4.4.2");
        capabilities.setCapability("device ID", "27688e90");
        
        driver = new AppiumDriver(new URL(url), capabilities);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }
}
