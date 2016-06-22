package com.mtjo.game.util;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.android.monkeyrunner.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by mtjo on 16-6-20.
 */
public class AdbUtil {

    public static IDevice connect() {
        // init the lib
        // [try to] ensure ADB is running
        String adbLocation = System.getProperty("com.android.screenshot.bindir"); //$NON-NLS-1$
        if (adbLocation != null && adbLocation.length() != 0)
            adbLocation += File.separator + "adb"; //$NON-NLS-1$
        else
            adbLocation = "adb"; //$NON-NLS-1$


        AndroidDebugBridge.init(false /* debugger support */);

        AndroidDebugBridge bridge = AndroidDebugBridge.createBridge(adbLocation, true /* forceNewBridge */);

        // we can't just ask for the device list right away, as the internal
        // thread getting
        // them from ADB may not be done getting the first list.
        // Since we don't really want getDevices() to be blocking, we wait
        // here manually.
        int count = 0;
        while (bridge.hasInitialDeviceList() == false)
            try{
        Thread.sleep(100);
        count++;}
        catch(InterruptedException e){}
        // pass


        // let's not wait > 10 sec.
        if (count > 100){
            System.err.println("Timeout getting device list!");
        return null;}


        // now get the devices
        IDevice[] devices = bridge.getDevices();

        if (devices.length == 0){
            System.out.println("No devices found!");
        return null;}


        return devices[0];
    }
}
