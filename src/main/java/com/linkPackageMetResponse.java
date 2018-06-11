package com;

/**
 * Created by kalistrat on 29.03.2018.
 */
public class linkPackageMetResponse {
    String mqttLog;
    String mqttPass;
    String mqttHost;
    String fromMqttTopic;
    String toMqttTopic;

    String temMqttTopic;
    String humMqttTopic;
    String lghtMqttTopic;

    int temTaskId;
    int humTaskId;
    int lghtTaskId;

    int leafId;
    String userName;


    public linkPackageMetResponse(

int oTemTaskId
, int oHumTaskId
, int oLghtTaskId
, String oPackLog
, String oPackPass
, String oPackHost
, String oPackFromTopic
, String oPackToTopic
, String oTemTopic
, String oHumTopic
, String oLghtTopic
, int oLeafId
, String UserName

    ){

        mqttLog = oPackLog;
        mqttPass = oPackPass;
        mqttHost = oPackHost;
        fromMqttTopic = oPackFromTopic;
        toMqttTopic = oPackToTopic;

        temMqttTopic = oTemTopic;
        humMqttTopic = oHumTopic;
        lghtMqttTopic = oLghtTopic;

        temTaskId = oTemTaskId;
        humTaskId = oHumTaskId;
        lghtTaskId = oLghtTaskId;

        leafId = oLeafId;
        userName = UserName;

    }
}
