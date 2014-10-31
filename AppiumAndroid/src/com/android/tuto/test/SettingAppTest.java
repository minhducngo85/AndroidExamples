package com.android.tuto.test;

import org.junit.Test;

import com.android.tuto.app.SettingApp;

public class SettingAppTest {

    static final String DEVICE_0_ID = "CB5A1QMDYP";
    static final String DEVICE_0_SW_VERSION = "4.4.4";
    
    //static final String DEVICE_1_ID = "4df175fb4a656f73";
    //static final String DEVICE_1_SW_VERSION = "4.1.2";

    @Test
    public void pairDevicesOverBT() throws Exception {
        SettingApp settingApp = new SettingApp(DEVICE_0_ID, DEVICE_0_SW_VERSION,"http://127.0.0.1:4723/wd/hub");
        settingApp.openSettingApp();
        //settingApp.turnOnBT();
        /** wait for pairing request from head unit*/
        //settingApp.waitForVisible(By.name("Bluetooth pairing request"), 100);
        //settingApp.acceptBTPairingrequest();
        //settingApp.quit();

        //settingApp.openSettingApp();
        //settingApp.enbaleWiFi();
        //settingApp.quit();
        
        SettingApp settingApp2 = new SettingApp("0146A0CD0F01B00F", "4.2.2","http://127.0.0.1:4725/wd/hub");
        settingApp2.openSettingApp();
        //settingApp2.turnOnBT();
    }

}
