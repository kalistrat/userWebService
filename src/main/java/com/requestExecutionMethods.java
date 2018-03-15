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

    public static boolean isChainCorrect(List<String> uidList,String userLogin){
        boolean chainCorrect = false;
        try {

            int k = 0;

            if (uidList.size() > 1) {

                for (int i = 1; i < uidList.size() - 1; i++) {
                    String pUID = getParentUID(uidList.get(i), userLogin);
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

            String lastItemPreffix = uidList.get(uidList.size()-1).substring(0,2);
            String penaultItemPreffix = uidList.get(uidList.size()-2).substring(0,2);

            if (uidList.size() == 1){

                if ((lastItemPreffix.equals("BRI"))||(lastItemPreffix.equals("SEN"))) {
                    linkPossible = true;
                }

            } else if (uidList.size() > 1) {

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
            if (isChainCorrect(UIDSList,userLogin)){
                if (isPossibleLink(UIDSList)){
                    linkResult = "DEVICE_IS_LINKED";
                } else {
                    linkResult = "WRONG_TYPE_CONNECTED_DEVICE";
                }
            } else {
                linkResult = "INVALID_DEVICE_SEQUENCE";
            }
        } catch (Exception e) {
            e.printStackTrace();
            linkResult = "EXECUTION_ERROR";
        }
        return linkResult;
    }

}
