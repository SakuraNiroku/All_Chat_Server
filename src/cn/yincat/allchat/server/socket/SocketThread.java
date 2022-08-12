package cn.yincat.allchat.server.socket;

import cn.yincat.allchat.server.Var;
import cn.yincat.allchat.server.tools.FriendModel;
import cn.yincat.allchat.server.tools.TokenModel;
import com.alibaba.fastjson.JSONObject;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
                *   "user":"preFriendName"
                * }
                *
                * Error 1. Token Error (tke_err)
                *
                * Error 2. FriendName Not Found (fnf_err)
                *
                * Error 3. Friend Found (fff_err)
                *
                * Finish (finish)
                * */

                /*
                * {
                *   "type":"friendAccept",
                *   "token":"token(uuid)",
                *   "user":"preFriendName"
                * }
                *
                * Error 1.Token Err (tke_err)
                * Error 2.FriendName Not Found(fnf_err)
                * finish(finish)
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
                        preparedStatement.setString(1, jsonObject.getString("user"));
                        preparedStatement.setString(2,md5Hex(jsonObject.getString("password")));
                        preparedStatement.setString(3,UUID.randomUUID().toString());
                        preparedStatement.executeUpdate();
                        JSONObject jsonObject1ret = new JSONObject();
                        jsonObject1ret.put("reqtype","finish");
                        printStream.println(jsonObject1ret.toJSONString());
                        client_s.close();
                        break;
                    case "login":
                        //检测用户名
                        ResultSet resultSetl = Var.mysqlVar.connection.createStatement().executeQuery("select * from user where User = '"+ jsonObject.getString("user")+"'");
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
                            jsonObject1fh.put("token",TokenModel.TokenUserTools(md5Hex(jsonObject.getString("user")),false));
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
                        PreparedStatement qwerty1234 = Var.mysqlVar.connection.prepareStatement("select * from Friend where user = ? and friendUser = ?");
                        qwerty1234.setString(1,TokenModel.TokenUserTools(jsonObject.getString("token"),true));
                        qwerty1234.setString(2,md5Hex(jsonObject.getString("user")));
                        ResultSet resultSetawdawdasdasdawa = qwerty1234.executeQuery();
                        if(resultSetawdawdasdasdawa.next()){
                            JSONObject jsonErr = new JSONObject();
                            jsonErr.put("reqtype","fff_err");
                            printStream.println(jsonErr.toJSONString());
                            client_s.close();
                        }

                         qwerty1234 = Var.mysqlVar.connection.prepareStatement("select * from PreFriend where name = ? and friendName = ?");
                        qwerty1234.setString(1,TokenModel.TokenUserTools(jsonObject.getString("token"),true));
                        qwerty1234.setString(2,jsonObject.getString("user"));
                        resultSetawdawdasdasdawa = qwerty1234.executeQuery();
                        if(resultSetawdawdasdasdawa.next()){
                            JSONObject jsonErr = new JSONObject();
                            jsonErr.put("reqtype","fff_err");
                            printStream.println(jsonErr.toJSONString());
                            client_s.close();
                        }

                        FriendModel.FriendAdd(jsonObject.getString("token"),jsonObject.getString("user"));
                        JSONObject jsonr = new JSONObject();
                        jsonr.put("reqtype","finish");
                        printStream.println(jsonr.toJSONString());
                        client_s.close();
                        break;
                    case "friendAccept":
                        if(!UserCheck(jsonObject.getString("token"),true)){
                            JSONObject jsonErr = new JSONObject();
                            jsonErr.put("reqtype","tke_err");
                            printStream.println(jsonErr.toJSONString());
                            client_s.close();
                        }
                        ArrayList<String> PreFriendList = FriendModel.GetPreFriendList(jsonObject.getString("uuid"));
                        if(!PreFriendList.contains(jsonObject.getString("user"))){
                            JSONObject jsonErr = new JSONObject();
                            jsonErr.put("reqtype","fnf_err");
                            printStream.println(jsonErr.toJSONString());
                            client_s.close();
                        }
                        PreparedStatement preparedStatement2 = Var.mysqlVar.connection.prepareStatement("delete from PreFriend where Name = ? and FriendName = ?");
                        preparedStatement2.setString(1, TokenModel.TokenUserTools(jsonObject.getString("token"),true));
                        preparedStatement2.setString(2,jsonObject.getString("user"));
                        preparedStatement2.executeUpdate();

                        {
                            PreparedStatement asaasawa = Var.mysqlVar.connection.prepareStatement("insert into Friend values (?,?)");
                            asaasawa.setString(1,TokenModel.TokenUserTools(jsonObject.getString("token"),true));
                            asaasawa.setString(2,jsonObject.getString("user"));
                            asaasawa.executeUpdate();
                        }

                        {
                            PreparedStatement asaasawa = Var.mysqlVar.connection.prepareStatement("insert into Friend values (?,?)");
                            asaasawa.setString(1,jsonObject.getString("user"));
                            asaasawa.setString(2,TokenModel.TokenUserTools(jsonObject.getString("token"),true));
                            asaasawa.executeUpdate();
                        }

                        JSONObject jsonr1 = new JSONObject();
                        jsonr1.put("reqtype","finish");
                        printStream.println(jsonr1);
                        client_s.close();
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
