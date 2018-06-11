package com;

/**
 * Created by kalistrat on 16.03.2018.
 */
public class linkResponse {

    String mqttLog;
    String mqttPass;
    String mqttHost;
    String mqttTopic;
    int leafId;
    int taskId;
    String mqttToTopic;
    String userName;

    public linkResponse(String devLog,String devPass, String devHost, String devTopic, int LeafId, int TaskId,String toTopic,String UserName){
        mqttLog = devLog;
        mqttPass = devPass;
        mqttHost = devHost;
        mqttTopic = devTopic;
        leafId = LeafId;
        taskId = TaskId;
        mqttToTopic = toTopic;
        userName = UserName;
    }
}
