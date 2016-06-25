# device-api-example
Demonstrates the initial provisioning and login of a device into HP PrintOS as well as sending real-time status.

# Overview
HP PrintOS exposes a set of APIs to allow devices to be created, log in, and send real-time statistics.  Those
APIs are exercised with this example project.  This example allows you to specify your (human) login and password
and create a device of a certain type and model in your PrintOS account, then send in some statistics about it
(current job count, printing status, and time to idle).

Typically, this is intended for device manufacturers that want their devices to be a part of the PrintOS ecosystem.
Having a device "provision" itself (get its own login and password) in this way allows the device to send in real-time
stats and possibly interact with other services in PrintOS.

A PrintOS-enabled device typically has a UI screen that allows the end-user to 'cloud-activate' the device.  This UI screen
consists of fields that allow the user to enter their PrintOS login and password, along with an 'Activate Now' button or something
similar.  When the user enters their login and password, the device first uses that to log in (one time only) to obtain
a PSP admin-capable security token, then that token is used in a header for a subsequent API call to create the device and 
receive back its own set of credentials (device login and password).  The device should securely store these, and then
the human's login/password is no longer needed.

Once the device has its own login/password, these can be used in the `/devices/login` call to log in the device and 
get a device token back.  (The token is really a session ID.).  The device provides this token as a header on all subsequent
API calls when sending real-time status to authenticate itself, handling the cases when its token expires.

## Do I Need To Do This To Accept Jobs?
No.  If all you want is for your device to be able to accept jobs from PrintOS, such as through PrintOS Box or SiteFlow,
it is not necessary for the device to go through the entire provisioning process.  If the device can accept jobs via
hotfolder, you can simply set up a Hotfolder device and configure Box or Siteflow to send to that hotfolder.  It is 
only if you want your device to show real-time stats (job count, printing status, time to completion) that you'll
need to support the provisioning process outlined here.

# Prerequisites
You should already have access to the PrintOS area of the HP Developer Portal ([hp.io](http://hp.io)).  You will also need an
invitation to create an account on our staging stack.  When you request access to PrintOS through [hp.io](http://hp.io), you
should get both of these.

When you accept the e-mail invitation, you'll create a login and password, and you'll set up your own PSP account,
in which you'll be a PSP admin.  PSP admin rights are required in order to manage devices.  You'll need to specify 
these credentials in a config file in the example code.

# Setup
**Required:** Once you download the example code, look for the file called `creds.properties`.  Edit this file and replace the
psp_login and psp_password fields with the login and password you created when you accepted the PrintOS invitation.

**Required:** Examine `settings.properties` and change any settings in there that you need to (e.g. proxy_enabled if
your environment requires it).  

# Running the Example
Once the setup steps are complete, simply run the main() method of `com.hp.printos.deviceapi.example.Main`.  This will
log in as you, create the device, log in as the device, and send statistics about the device (job count, time to
completion, and printing status).  This will create the device in your organization's account.  Once it's there, you
can log in to the integration server at  [https://stg.printopt.org](https://stg.printopt.org) with your PSP admin login 
and password, and go to 'Account'
in the app switcher and go to 'Devices'.  You'll see the device in the list, as well as on the home page along with
the status you supplied.

![Device List Screenshot](https://raw.githubusercontent.com/printos/device-api-example/master/etc/images/device_list.png)

![Device Tile Screenshot](https://raw.githubusercontent.com/printos/device-api-example/master/etc/images/device_tile.png)

# Switching to Production
In `settings.properties`, you'll see the following entry:

```
base_url=https://stage.printos.api.hp.com/platform
```

Once your development is done, you can point to production by removing the 'stage', e.g.

```
base_url=https://printos.api.hp.com/platform
```

Note that you will need a separate account invitation to production in order to view devices on that stack, so you'll
also want to update the `psp_login` and `psp_password` in `creds.properties` as well with the new account information.

# Best Practices
Typically, PrintOS-enabled devices will have a UI screen on the device itself to allow the user to enter their
credentials to provision the device.  Devices should store their own device login and password securely encrypted
on the device itself.  The user (human) credentials should never be stored on the device and should never be used
except for the initial provisioning of the device into PrintOS.

Once logged in, the device will get its token expiration back from the server and should use that information as a
hint to handle the expiration of the token.  The device will need to gracefully handle the expiration of a session
token and log in again to get a new one.  Do not log in every time you need to send status; use the token you already
have if it's not expired.

Devices should not send status more often than every 1-2 minutes.