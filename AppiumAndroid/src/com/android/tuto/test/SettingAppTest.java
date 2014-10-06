package com.android.tuto.test;

import org.junit.Test;
import org.openqa.selenium.By;

import com.android.tuto.app.SettingApp;

public class SettingAppTest {

    static final String DEVICE_0_ID = "27688e90";
    static final String DEVICE_0_SW_VERSION = "4.4.2";
    
    //static final String DEVICE_1_ID = "4df175fb4a656f73";
    //static final String DEVICE_1_SW_VERSION = "4.1.2";

    @Test
    public void apiDemo() throws Exception {
        SettingApp settingApp = new SettingApp(DEVICE_0_ID, DEVICE_0_SW_VERSION);
        settingApp.openSettingApp();
        settingApp.turnOnBT();
        settingApp.waitForVisible(By.name("Bluetooth pairing request"), 50);
        settingApp.acceptBTPairingrequest();
        settingApp.quit();

        //settingApp.openSettingApp();
        //settingApp.enbaleWiFi();
        //settingApp.quit();
    }

}
