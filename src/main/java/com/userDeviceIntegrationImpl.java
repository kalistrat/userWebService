
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
        try {
            if (servLogin.equals(requestExecutionMethods.SERVLOG) && servPassword.equals(requestExecutionMethods.SERVPASS)) {

                requestExecutionMethods.addNewUserFromSite(userLogin, userPassword, userMail, userPhone);
                requestExecutionMethods.sendMessAgeToSubcribeServer(
                        777
                        , userLogin
                        , "add"
                        , "server"
                );
                return "USER_WAS_ADDED";
            } else {
                return "WRONG_LOGIN_PASSWORD";
            }
        } catch (Exception e){
            e.printStackTrace();
            return "EXECUTION_ERROR";
        }
    }
}

