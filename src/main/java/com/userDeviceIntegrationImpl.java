
package com;

import javax.jws.WebService;

@WebService(endpointInterface = "com.userDeviceIntegration")
public class userDeviceIntegrationImpl implements userDeviceIntegration {

    public String linkUserDevice(String UID_CHAIN,String userLogin,String userPassword) {
        return "Hello " + UID_CHAIN;
    }
}

