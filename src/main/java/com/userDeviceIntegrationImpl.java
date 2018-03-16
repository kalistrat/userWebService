
package com;

import javax.jws.WebService;

@WebService(endpointInterface = "com.userDeviceIntegration")
public class userDeviceIntegrationImpl implements userDeviceIntegration {

    public String linkUserDevice(String UID_CHAIN
            ,String userLogin
            ,String userPassword
    ) {
        try {
            String dbUserPass = requestExecutionMethods.getUserPassSha(userLogin);
            String resPassSha = requestExecutionMethods.sha256(userPassword);

            if (dbUserPass != null && resPassSha != null) {
                if (dbUserPass.equals(resPassSha)) {
                    return requestExecutionMethods.linkExecute(UID_CHAIN,userLogin);
                } else {
                    return "WRONG_LOGIN_PASSWORD";
                }
            } else {
                return "WRONG_LOGIN_PASSWORD";
            }
        } catch (Exception e){
            e.printStackTrace();
            return "EXECUTION_ERROR(linkUserDevice)";
        }

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

