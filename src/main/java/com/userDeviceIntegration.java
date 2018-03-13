package com;

import javax.jws.WebService;

@WebService
public interface userDeviceIntegration {
    String linkUserDevice(String uid_chain,String servLogin,String servPassword);
    String addNewUser(String userLogin
            ,String userPassword
            ,String userMail
            ,String userPhone
            ,String servLogin
            ,String servPassword
    );
}

