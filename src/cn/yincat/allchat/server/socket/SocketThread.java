package cn.yincat.allchat.server.socket;

import cn.yincat.allchat.server.Var;
import cn.yincat.allchat.server.tools.FriendModel;
import com.alibaba.fastjson.JSONObject;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SocketThread extends Thread{
    Socket client_s;
    public SocketThread(Socket cilent_s){
        client_s = cilent_s;
    }

    public boolean UserCheck(String string,boolean uuidcheck){
        if(uuidcheck){
            try {
                ResultSet resultSet = Var.mysqlVar.connection.createStatement().executeQuery("select * from user where uuid = '"+string+"'");
                if(resultSet.next()){
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            try {
                ResultSet resultSet = Var.mysqlVar.connection.createStatement().executeQuery("select * from user where User = '"+md5Hex(string)+"'");
                if(resultSet.next()){
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void run() {
        while(true){
            try{
                if(client_s.isClosed()){
                    break;
                }

                /*
                * {
                *   "type":"register",
                *   "user":"a",
                *   "password":"pass"
                * }
                *
                *   Error User Found uf_err
                *
                *   Finish:
                *   {
                *       "reqtype":"finish"
                *   }
                * */

                /*
                * {
                *   "type":"login",
                *   "user":"a",
                *   "password":"pass"
                * }
                *
                *   Error 1.User Not Fount
                *  {
                *   "reqtype":"unf_err"
                *  }
                *
                *   Error 2.Password Not Right
                * {
                *   "reqtype":"pnr_err"
                * }
                *
                * Finish
                * {
                *   "reqtype":"finish",
                *   "token":"token(uuid)"
                * */

                //Friend System

                /*
                * {
                *   "type":"friendAdd",
                *   "token":"token(uuid)"
                *   "user":"friendName"
                * }
                *
                * Error 1. Token Error (tke_err)
                *
                * Error 2. FriendName Not Found (fnf_err)
                *
                * Finish (finish)
                * */

                DataInputStream dataInputStream = new DataInputStream(client_s.getInputStream());
                PrintStream printStream = new PrintStream(client_s.getOutputStream());

                JSONObject jsonObject = JSONObject.parseObject(dataInputStream.readLine());

                switch (jsonObject.getString("type")){
                    case "register":
                        //检测用户名
                        if(UserCheck(md5Hex(jsonObject.getString("user")),false)){
                            JSONObject jsonObject1fh = new JSONObject();
                            jsonObject1fh.put("reqtype","uf_err");
                            printStream.println(jsonObject1fh.toJSONString());
                            client_s.close();
                        }
                        PreparedStatement preparedStatement = Var.mysqlVar.connection.prepareStatement("insert into user values (?,?,?)");
                        preparedStatement.setString(1, md5Hex(jsonObject.getString("user")));
                        preparedStatement.setString(2,md5Hex(jsonObject.getString("password")));
                        preparedStatement.setString(3,UUID.randomUUID().toString());
                        preparedStatement.executeUpdate();
                        PreparedStatement preparedStatement1 = Var.mysqlVar.connection.prepareStatement("CREATE TABLE ? (\n" +
                                "  `uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,\n" +
                                "  `chat_uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,\n" +
                                "  PRIMARY KEY (`uuid`,`chat_uuid`),\n" +
                                "  KEY `usert_a_ibfk_2` (`chat_uuid`),\n" +
                                "  CONSTRAINT `usert_a_ibfk_1` FOREIGN KEY (`uuid`) REFERENCES `user` (`User`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                                "  CONSTRAINT `usert_a_ibfk_2` FOREIGN KEY (`chat_uuid`) REFERENCES `cuuid` (`cuuid`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");
                        preparedStatement1.setString(1,md5Hex(jsonObject.getString("user")));
                        preparedStatement1.execute();
                        JSONObject jsonObject1ret = new JSONObject();
                        jsonObject1ret.put("reqtype","finish");
                        printStream.println(jsonObject1ret.toJSONString());
                        client_s.close();
                        break;
                    case "login":
                        //检测用户名
                        ResultSet resultSetl = Var.mysqlVar.connection.createStatement().executeQuery("select * from user where User = '"+md5Hex(jsonObject.getString("user"))+"'");
                        if(!resultSetl.next()){
                            JSONObject jsonObject1fh = new JSONObject();
                            jsonObject1fh.put("reqtype","unf_err");
                            printStream.println(jsonObject1fh.toJSONString());
                            client_s.close();
                        }
                        if(!resultSetl.getString("PasswordJ").equals(md5Hex(jsonObject.getString("password")))){
                            JSONObject jsonObject1fh = new JSONObject();
                            jsonObject1fh.put("reqtype","pnr_err");
                            printStream.println(jsonObject1fh.toJSONString());
                            client_s.close();
                        }else{
                            JSONObject jsonObject1fh = new JSONObject();
                            jsonObject1fh.put("reqtype","finish");
                            printStream.println(jsonObject1fh.toJSONString());
                            client_s.close();
                        }
                        break;
                    case "friendAdd":
                        if(!UserCheck(jsonObject.getString("token"),true)){
                            JSONObject jsonErr = new JSONObject();
                            jsonErr.put("reqtype","tke_err");
                            printStream.println(jsonErr.toJSONString());
                            client_s.close();
                        }
                        if(!UserCheck(jsonObject.getString("user"),false)){
                            JSONObject jsonErr = new JSONObject();
                            jsonErr.put("reqtype","fnf_err");
                            printStream.println(jsonErr.toJSONString());
                            client_s.close();
                        }
                        FriendModel.FriendAdd(jsonObject.getString("token"),jsonObject.getString("user"));
                        break;

                }


            }catch (SocketException e){
                e.printStackTrace();
                return;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}