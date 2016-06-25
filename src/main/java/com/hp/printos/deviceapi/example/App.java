package com.hp.printos.deviceapi.example;

import com.hp.printos.deviceapi.example.util.ClientWrapper;
import com.hp.printos.deviceapi.example.util.Utils;
import org.apache.http.client.HttpClient;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

public class App {
  public static void main(String[] args) {
    PropertyConfigurator.configure("log4j.xml");

    System.out.println("PrintOS device API example\n");
    System.out.println("Before running, review " + Constants.SETTINGS_FILE + " and " + Constants.CREDS_FILE);
    System.out.println("and change the settings as appropriate.\n");

    Properties settings = Utils.getProperties(Constants.SETTINGS_FILE);
    Properties credentials = Utils.getProperties(Constants.CREDS_FILE);
    Utils.checkCredentials(credentials);    // Make sure the user has edited creds.properties

    String pspLogin = credentials.getProperty("psp_login");
    String pspPassword = credentials.getProperty("psp_password");

    try {

      // Step 1.  Log in as the PSP administrator.

      System.out.println("Logging in as user " + pspLogin + ".");
      HttpClient client = ClientWrapper.getClient(settings);
      String url = settings.getProperty("base_url") + Constants.AAA_PATH + "/users/login";
      String userToken = User.loginAndGetToken(client, url, pspLogin, pspPassword);
      Utils.checkToken(userToken);

      // Step 2.  Create the device.

      System.out.println("Creating device \"" + settings.getProperty("device_name") + "\".\n");
      DeviceInfo deviceInfo = Device.create(settings.getProperty("base_url") + Constants.AAA_PATH, client, userToken, settings, credentials);
      Utils.checkDevice(deviceInfo);

      // Step 3.  Log in as the device.

      url = settings.getProperty("base_url") + Constants.AAA_PATH + "/devices/login";
      String deviceToken = User.loginAndGetToken(client, url, deviceInfo.getDeviceLogin(), deviceInfo.getDevicePassword());
      Utils.checkToken(deviceToken);

      // Step 4.  Send in some statistics for the device.

      url = settings.getProperty("base_url") + Constants.SS_PATH + "/devices";
      Device.postRealTimeData(client, url, deviceToken);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
