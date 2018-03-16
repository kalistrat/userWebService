package com;

/**
 * Created by kalistrat on 16.03.2018.
 */
public class linkResponse {

    String mqttLog;
    String mqttPass;
    String mqttHost;
    String mqttTopic;
    int treeId;

    public linkResponse(String devLog,String devPass, String devHost, String devTopic, int TreeId){
        mqttLog = devLog;
        mqttPass = devPass;
        mqttHost = devHost;
        mqttTopic = devTopic;
        treeId = TreeId;
    }
}
