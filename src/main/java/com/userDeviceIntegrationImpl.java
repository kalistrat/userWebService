
package com;

import javax.jws.WebService;

@WebService(endpointInterface = "com.userDeviceIntegration")
public class userDeviceIntegrationImpl implements userDeviceIntegration {

    public String linkUserDevice(String UID_CHAIN
            ,String servLogin
            ,String servPassword
    ) {
        return "Hello " + UID_CHAIN;
    }

    public String addNewUser(String userLogin
            ,String userPassword
            ,String userMail
            ,String userPhone
            ,String servLogin
            ,String servPassword
    ){
        if (servLogin.equals(requestExecutionMethods.SERVLOG) && servPassword.equals(requestExecutionMethods.SERVPASS)){

            requestExecutionMethods.addNewUserFromSite(userLogin,userPassword,userMail,userPhone);
            return "USER_WAS_ADDED";
        } else {
            return "WRONG_LOGIN_PASSWORD";
        }
    }
}

