package com.vwinfotainment.swtest.func;

import io.appium.java_client.AppiumDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SettingApp {

    private static final String url = "http://127.0.0.1:4723/wd/hub";

    private String deviceId = "";

    private String swVersion = "";

    private AppiumDriver driver = null;

    private DesiredCapabilities capabilities;

    /**
     * @return the driver
     */
    public AppiumDriver getDriver() {
        return driver;
    }

    public SettingApp(String deviceId, String swVersion) {
        super();
        this.deviceId = deviceId;
        this.swVersion = swVersion;
        /** Setup */

        capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.BROWSER_NAME, "");
        capabilities.setCapability(CapabilityType.PLATFORM, "Android");
        capabilities.setCapability(CapabilityType.VERSION, this.swVersion);
        capabilities.setCapability("deviceName", "Android Emulator");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceType", "Phone");
        capabilities.setCapability("appPackage", "com.android.settings");
        capabilities.setCapability("appActivity", ".Settings");
        // for real device
        capabilities.setCapability("device ID", this.deviceId);

        // capabilities = new DesiredCapabilities();
        // //capabilities.setCapability("appium-version", "1.1");
        // capabilities.setCapability("automationName", "Selendroid");
        // capabilities.setCapability("platformName", "Android");
        // capabilities.setCapability("deviceType", "Phone");
        // capabilities.setCapability("deviceName", "Android Emulator");
        // capabilities.setCapability("platformVersion", "4.1");
        // capabilities.setCapability("device ID", this.deviceId);
        // capabilities.setCapability("appPackage", "com.android.settings");
        // capabilities.setCapability("appActivity", ".Settings");
    }

    public void quit() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * to open setting application
     * 
     * @throws MalformedURLException
     */
    public void openSettingApp() throws MalformedURLException {
        driver = new AppiumDriver(new URL(url), capabilities);
    }

    /**
     * Precondition: setting application is opened.
     * 
     * @throws MalformedURLException
     * @throws InterruptedException
     */
    public void turnOnBT() throws MalformedURLException, InterruptedException {
        /** run steps */
        WebElement bluetooth = driver.findElement(By.name("Bluetooth"));
        bluetooth.click();

        WebElement switchOnOff = driver.findElement(By.className("android.widget.Switch"));
        if (!switchOnOff.getText().equalsIgnoreCase("on")) {
            switchOnOff.click();
            waitForVisible(By.name("Only visible to paired devices. Tap to make visible to other devices."), 10);
        }
        WebElement checkboxEl = driver.findElement(By.className("android.widget.CheckBox"));
        if (checkboxEl.getAttribute("checked").equalsIgnoreCase("false")) {
            checkboxEl.click();
        }
        Thread.sleep(3000);
    }

    /**
     * Precondition: setting application is opened.
     * 
     * @throws MalformedURLException
     * @throws InterruptedException
     */
    public void enbaleWiFi() throws InterruptedException {
        /** run steps */
        WebElement wifi = driver.findElement(By.name("Wi-Fi"));
        wifi.click();

        WebElement switchOnOff = driver.findElement(By.className("android.widget.Switch"));
        if (!switchOnOff.getText().equalsIgnoreCase("on")) {
            switchOnOff.click();
            waitForVisible(By.name("Wi-Fi networks"), 10);
        }
        WebElement checkboxEl = driver.findElement(By.className("android.widget.CheckBox"));
        if (checkboxEl.getAttribute("checked").equalsIgnoreCase("false")) {
            checkboxEl.click();
        }
        Thread.sleep(3000);
    }

    /**
     * precondition: the Bluetooth paring request dialog is on the screen.
     */
    public void acceptBTPairingrequest() {
        WebElement ok = driver.findElement(By.xpath("//android.widget.Button[contains(@text,'OK') or contains(@text,'Pair')]"));
        ok.click();

    }

    /**
     * precondition: the Bluetooth paring request dialog is on the screen.
     */
    public void rejectBTPairingrequest() {
        WebElement ok = driver.findElement(By.xpath("//android.widget.Button[contains(@text,'Cancel')]"));
        ok.click();
    }

    public void waitForVisible(final By by, int waitTime) {
        int timeoutInSeconds = 90;
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

}
