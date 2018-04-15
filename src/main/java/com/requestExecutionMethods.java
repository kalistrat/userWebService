package com;

import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalistrat on 13.03.2018.
 */
public class requestExecutionMethods {

    public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static final String DB_URL = "jdbc:mysql://localhost/things";
    public static final String USER = "kalistrat";
    public static final String PASS = "045813";

    public static final String SERVPASS = "BHUerg567Sdc";
    public static final String SERVLOG = "userWeb045813";

    public static void addNewUserFromSite(
            String iUserLogin
            ,String iUserPasswordSha
            ,String iUserMail
            ,String iUserPhone
    ){
        try {

            Class.forName(JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    DB_URL
                    , USER
                    , PASS
            );

            CallableStatement Stmt = Con.prepareCall("{call addNewUserFromSite(?, ?, ?, ?)}");
            Stmt.setString(1, iUserLogin);
            Stmt.setString(2, iUserPasswordSha);
            Stmt.setString(3, iUserMail);
            Stmt.setString(4,iUserPhone);
            Stmt.execute();

            Con.close();


        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();

        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();

        }
    }



    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static List<String> GetListFromString(String DevidedString, String Devider){
        List<String> StrPieces = new ArrayList<String>();
        try {
            int k = 0;
            String iDevidedString;
            // 123|321|456|

            if (DevidedString.startsWith(Devider)) {
                DevidedString = DevidedString.substring(1,DevidedString.length());
            }

            if (!DevidedString.contains(Devider)) {
                iDevidedString = DevidedString + Devider;
            } else {
                if (!DevidedString.endsWith(Devider)) {
                    iDevidedString = DevidedString + Devider;
                } else {
                    iDevidedString = DevidedString;
                }
            }

            while (!iDevidedString.equals("")) {
                int Pos = iDevidedString.indexOf(Devider);
                StrPieces.add(iDevidedString.substring(0, Pos));
                iDevidedString = iDevidedString.substring(Pos + 1);
                k = k + 1;
                if (k > 100000) {
                    iDevidedString = "";
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return StrPieces;
    }

    public static String sendMessAgeToSubcribeServer(
            int qEntityId
            ,String qUserLog
            ,String qActionType
            ,String MessAgeType
    ){
        try {

            Socket s = new Socket("localhost", 3128);
            String InMessageValue = qActionType + "/" + qUserLog + "/" + MessAgeType +"/" + String.valueOf(qEntityId) + "/";


            s.getOutputStream().write(InMessageValue.getBytes());
            // читаем ответ
            byte buf[] = new byte[256 * 1024];
            int r = s.getInputStream().read(buf);
            String outSubscriberMessage = new String(buf, 0, r);
            List<String> MessageAttr = GetListFromString(outSubscriberMessage,"|");
            //System.out.println("Is operation Sussess :" + MessageAttr.get(0));
            //System.out.println("Operation Message:" + MessageAttr.get(0));
            s.close();

            if (MessageAttr.get(0).equals("N")) {
                return MessageAttr.get(1);
            } else {
                return "";
            }

        }
        catch(IOException e) {
            return "Ошибка подключения к серверу подписки";
        }
    }

    public static String getUserPassSha(String userLog){
        String passSha = null;
        try {

            Class.forName(JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    DB_URL
                    , USER
                    , PASS
            );

            CallableStatement Stmt = Con.prepareCall("{? = call f_get_user_password(?)}");
            Stmt.registerOutParameter(1, Types.VARCHAR);
            Stmt.setString(2, userLog);
            Stmt.execute();
            passSha = Stmt.getString(1);

            Con.close();


        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();

        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();

        }
        return passSha;
    }

    public static String getParentUID(String UID,String userLog){
        String parentUID = null;
        try {

            Class.forName(JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    DB_URL
                    , USER
                    , PASS
            );

            CallableStatement Stmt = Con.prepareCall("{? = call getParentUID(? ,?)}");
            Stmt.registerOutParameter(1, Types.VARCHAR);
            Stmt.setString(2, UID);
            Stmt.setString(3, userLog);
            Stmt.execute();

            parentUID = Stmt.getString(1);
            Con.close();


        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();

        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();

        }
        return parentUID;
    }

    public static String getLeafType(String UID,String userLog){
        String LeafType = null;
        try {

            Class.forName(JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    DB_URL
                    , USER
                    , PASS
            );

            CallableStatement Stmt = Con.prepareCall("{? = call getLeafType(? ,?)}");
            Stmt.registerOutParameter(1, Types.VARCHAR);
            Stmt.setString(2, UID);
            Stmt.setString(3, userLog);
            Stmt.execute();

            LeafType = Stmt.getString(1);
            Con.close();
            if (LeafType == null){
                LeafType = "";
            }


        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();

        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();

        }
        return LeafType;
    }

    public static boolean isChainCorrect(List<String> uidList,String userLogin){
        boolean chainCorrect = false;
        try {

            int k = 0;

            if (uidList.size() > 2) {

                for (int i = 1; i < uidList.size() - 1; i++) {
                    String pUID = getParentUID(uidList.get(i), userLogin);
                    //System.out.println("pUID with size > 2 : " + pUID);
                    if (pUID != null) {
                        if (pUID.equals(uidList.get(i - 1))) {
                            k = k + 1;
                        }
                    }
                }

                if (k == uidList.size() - 2) {
                    String parentLastUID = getParentUID(uidList.get(uidList.size()-1), userLogin);
                    if (parentLastUID != null) {
                        if (parentLastUID.equals(userLogin)) {
                            chainCorrect = true;
                        }
                    }
                }

            } else if (uidList.size() == 1) {
                String pUID = getParentUID(uidList.get(0), userLogin);
                if (pUID != null) {
                    if (pUID.equals(userLogin)) {
                        chainCorrect = true;
                    }
                }
            } else if (uidList.size() == 2) {
                String pUID = getParentUID(uidList.get(0), userLogin);
                String leafTypeFirstItem = getLeafType(uidList.get(0), userLogin);
                //System.out.println("pUID : " + pUID);
                //System.out.println("leafTypeFirstItem : " + leafTypeFirstItem);

                if (pUID != null) {
                    if (pUID.equals(userLogin) && leafTypeFirstItem.equals("FOLDER")) {
                        chainCorrect = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //fail
        }
        return chainCorrect;

    }

    public static boolean isPossibleLink(List<String> uidList){
        boolean linkPossible = false;
        try {

            String lastItemPreffix;
            String penaultItemPreffix;

            if (uidList.size() == 1){
                lastItemPreffix = uidList.get(uidList.size()-1).substring(0,3);

                //System.out.println("lastItemPreffix : " + lastItemPreffix);

                if (lastItemPreffix.equals("BRI")) {
                    linkPossible = true;
                }

                if (lastItemPreffix.equals("MET")) {
                    linkPossible = true;
                }

            } else if (uidList.size() > 1) {

                lastItemPreffix = uidList.get(uidList.size()-1).substring(0,3);
                penaultItemPreffix = uidList.get(uidList.size()-2).substring(0,3);

                //System.out.println("lastItemPreffix : " + lastItemPreffix);
                //System.out.println("penaultItemPreffix : " + penaultItemPreffix);


                if (penaultItemPreffix.equals("BRI") && (lastItemPreffix.equals("SEN"))) {
                    linkPossible = true;
                } else if (penaultItemPreffix.equals("BRI") && (lastItemPreffix.equals("RET"))){
                    linkPossible = true;
                } else if (penaultItemPreffix.equals("RET") && (lastItemPreffix.equals("SEN"))){
                    linkPossible = true;
                } else if (penaultItemPreffix.equals("RET") && (lastItemPreffix.equals("RET"))){
                    linkPossible = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return linkPossible;
    }

    public static String linkExecute(String uidChain,String userLogin){
        String linkResult;
        try {
            List<String> UIDSList = GetListFromString(uidChain, "|");

            //for (String iUID : UIDSList)
            //System.out.println("iUID : " + iUID);

            if (isChainCorrect(UIDSList,userLogin)){
                if (isPossibleLink(UIDSList)){
                    if (getLeafType(UIDSList.get(UIDSList.size()-1),userLogin).equals("LEAF")) {


                        String lastItemPreffix = UIDSList.get(UIDSList.size() - 1).substring(0, 3);
                        if (UIDSList.size() == 1) {
                            if (lastItemPreffix.equals("BRI")) {
                                linkResponse resp = updateLeaf(userLogin, UIDSList.get(0), userLogin);

                                linkResult = "mqttLogin : " + resp.mqttLog + ";\n"
                                        + "mqttPassword : " + resp.mqttPass + ";\n"
                                        + "mqttHost : " + resp.mqttHost + ";\n"
                                        + "fromServerTopic : " + resp.mqttTopic + ";\n"
                                        + "toServerTopic : " + resp.mqttToTopic + ";";

                                sendMessAgeToSubcribeServer(
                                        resp.leafId
                                        , userLogin
                                        , "change"
                                        , "server"
                                );

                            } else if (lastItemPreffix.equals("MET"))  {
                                linkPackageMetResponse respMet = updateMeteoPackage(userLogin, UIDSList.get(0), userLogin);

                                linkResult = "mqttLogin : " + respMet.mqttLog + ";\n"
                                        + "mqttPassword : " + respMet.mqttPass + ";\n"
                                        + "mqttHost : " + respMet.mqttHost + ";\n"
                                        + "fromServerTopic : " + respMet.fromMqttTopic + ";\n"
                                        + "toServerTopic : " + respMet.toMqttTopic + ";\n"
                                        + "temTopic : " + respMet.temMqttTopic + ";\n"
                                        + "humTopic : " + respMet.humMqttTopic + ";\n"
                                        + "lghtTopic : " + respMet.lghtMqttTopic + ";";

                                sendMessAgeToSubcribeServer(
                                        respMet.leafId
                                        , userLogin
                                        , "change"
                                        , "server"
                                );


                            } else {
                                linkResult = "ERROR_WRONG_TYPE_CONNECTED_DEVICE";
                            }
                        } else {
                            linkResponse resp = updateLeaf(UIDSList.get(UIDSList.size() - 2), UIDSList.get(UIDSList.size() - 1), userLogin);
                            sendMessAgeToSubcribeServer(
                                    resp.taskId
                                    , userLogin
                                    , "add"
                                    , "task"
                            );
                            linkResult = "mqttLogin : " + resp.mqttLog + ";\n"
                                    + "mqttPassword : " + resp.mqttPass + ";\n"
                                    + "mqttHost : " + resp.mqttHost + ";\n"
                                    + "fromServerTopic : " + resp.mqttTopic + ";\n"
                                    + "toServerTopic : " + resp.mqttToTopic + ";";

                        }

                    } else {
                        linkResult = "ERROR_DEVICE_ALREADY_ATTACHED";
                    }
                } else {
                    linkResult = "ERROR_WRONG_TYPE_CONNECTED_DEVICE";
                }
            } else {
                linkResult = "ERROR_INVALID_DEVICE_SEQUENCE";
            }
        } catch (Exception e) {
            e.printStackTrace();
            linkResult = "ERROR_EXECUTION";
        }
        return linkResult;
    }

    public static linkResponse updateLeaf(String parentUID,String childUID,String userLog){
        linkResponse responseValue = null;
        try {

            Class.forName(JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    DB_URL
                    , USER
                    , PASS
            );

            CallableStatement Stmt = Con.prepareCall("{call updateLeaf(?,?,?,?,?,?,?,?,?,?)}");
            Stmt.setString(1, parentUID);
            Stmt.setString(2, childUID);
            Stmt.setString(3, userLog);
            Stmt.registerOutParameter(4, Types.VARCHAR);
            Stmt.registerOutParameter(5, Types.VARCHAR);
            Stmt.registerOutParameter(6, Types.VARCHAR);
            Stmt.registerOutParameter(7, Types.VARCHAR);
            Stmt.registerOutParameter(8, Types.INTEGER);
            Stmt.registerOutParameter(9, Types.INTEGER);
            Stmt.registerOutParameter(10, Types.VARCHAR);

            Stmt.execute();

            responseValue = new linkResponse(
                    Stmt.getString(4)
                    ,Stmt.getString(5)
                    ,Stmt.getString(6)
                    ,Stmt.getString(7)
                    ,Stmt.getInt(8)
                    ,Stmt.getInt(9)
                    ,Stmt.getString(10)
            );
            Con.close();


        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();

        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();

        }
        return responseValue;
    }

    public static linkPackageMetResponse updateMeteoPackage(String parentUID,String childUID,String userLog){
        linkPackageMetResponse responseValue = null;
        try {

            Class.forName(JDBC_DRIVER);
            Connection Con = DriverManager.getConnection(
                    DB_URL
                    , USER
                    , PASS
            );

            CallableStatement Stmt = Con.prepareCall("{call updateMeteoPackage(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            Stmt.setString(1, parentUID);
            Stmt.setString(2, childUID);
            Stmt.setString(3, userLog);
            Stmt.registerOutParameter(4, Types.INTEGER);
            Stmt.registerOutParameter(5, Types.INTEGER);
            Stmt.registerOutParameter(6, Types.INTEGER);
            Stmt.registerOutParameter(7, Types.VARCHAR);
            Stmt.registerOutParameter(8, Types.VARCHAR);
            Stmt.registerOutParameter(9, Types.VARCHAR);
            Stmt.registerOutParameter(10, Types.VARCHAR);
            Stmt.registerOutParameter(11, Types.VARCHAR);
            Stmt.registerOutParameter(12, Types.VARCHAR);
            Stmt.registerOutParameter(13, Types.VARCHAR);
            Stmt.registerOutParameter(14, Types.VARCHAR);
            Stmt.registerOutParameter(15, Types.INTEGER);

            Stmt.execute();

            responseValue = new linkPackageMetResponse(
                    Stmt.getInt(4)
                    ,Stmt.getInt(5)
                    ,Stmt.getInt(6)
                    ,Stmt.getString(7)
                    ,Stmt.getString(8)
                    ,Stmt.getString(9)
                    ,Stmt.getString(10)
                    ,Stmt.getString(11)
                    ,Stmt.getString(12)
                    ,Stmt.getString(13)
                    ,Stmt.getString(14)
                    ,Stmt.getInt(15)
            );
            Con.close();


        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();

        }catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();

        }
        return responseValue;
    }

}
