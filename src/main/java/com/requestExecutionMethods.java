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

    public static List<String> GetListFromString(String DevidedString,String Devider){
        List<String> StrPieces = new ArrayList<String>();
        int k = 0;
        String iDevidedString = DevidedString;

        if (DevidedString.contains(Devider)) {

            while (!iDevidedString.equals("")) {
                int Pos = iDevidedString.indexOf(Devider);
                StrPieces.add(iDevidedString.substring(0, Pos));
                iDevidedString = iDevidedString.substring(Pos + 1);
                k = k + 1;
                if (k > 100000) {
                    iDevidedString = "";
                }
            }
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
            passSha = Stmt.getString(1);
            Stmt.execute();

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
            parentUID = Stmt.getString(1);
            Stmt.execute();

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

    public void linkExecute(String uidChain,String userLogin){
        List<String> uidList = GetListFromString(uidChain,"|");
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

            if (k == uidList.size() - 1) {
                // success
            } else {
                // fail
            }

        } else if (uidList.size() == 1) {
            String pUID = getParentUID(uidList.get(0), userLogin);
            if (pUID != null) {
                if (pUID.equals(userLogin)) {
                  // success
                } else {
                    //fail
                }
            } else {
                //fail
            }
        } else {
            //fail
        }


    }

}
