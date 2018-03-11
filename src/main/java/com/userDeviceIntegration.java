package com;

import javax.jws.WebService;

@WebService
public interface userDeviceIntegration {
    String linkUserDevice(String uid_chain,String login,String password);
}

