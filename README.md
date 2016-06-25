# device-api-example
Demonstrates the initial provisioning and login of a device into HP PrintOS as well as sending real-time status.

# Overview
HP PrintOS exposes a set of APIs to allow devices to be created, log in, and send real-time statistics.  Those
APIs are exercised with this example project.  This example allows you to specify your (human) login and password
and create a device of a certain type and model in your PrintOS account, then send in some statistics about it
(current job count, printing status, and time to idle).

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
completion, and printing status).

# Best Practices
Typically, PrintOS-enabled devices will have a UI screen on the device itself to allow the user to enter their
credentials to provision the device.  Devices should store their own device login and password securely encrypted
on the device itself.  The user (human) credentials should never be stored on the device and should never be used
except for the initial provisioning of the device into PrintOS.

Once logged in, the device will get its token expiration back from the server and should use that information as a
hint to handle the expiration of the token.  The device will need to gracefully handle the expiration of a session
token and log in again to get a new one.  Do not log in every time you need to send status; use the token you already
have if it's not expired.

Devices should not send status more often than every 1-2 minutes.  Please do not spam the servers.