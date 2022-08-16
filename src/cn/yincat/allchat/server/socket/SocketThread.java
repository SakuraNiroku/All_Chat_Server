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

//代码无注释，自己看awa
@SuppressWarnings("all")
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
                ResultSet resultSet = Var.mysqlVar.connection.createStatement().executeQuery("select * from user where User = '"+string+"'");
                if(resultSet.next()){
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void Finish(PrintStream printStream,Socket socket){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("reqtype","finish");
        printStream.println(jsonObject.toJSONString());
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            try{
                if(client_s.isClosed()){
                    break;
                }
                DataInputStream dataInputStream = new DataInputStream(client_s.getInputStream());
                PrintStream printStream = new PrintStream(client_s.getOutputStream());

                JSONObject jsonObject = JSONObject.parseObject(dataInputStream.readLine());

                switch (jsonObject.getString("type")){
                    case "register":

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
                        Finish(printStream,client_s);

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
                            jsonObject1fh.put("token",TokenModel.TokenUserTools(jsonObject.getString("user"),false));
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
                        Finish(printStream,client_s);
                        break;
                    case "friendAccept":
                        System.out.println(jsonObject.getString("token"));
                        if(!UserCheck(jsonObject.getString("token"),true)){
                            JSONObject jsonErr = new JSONObject();
                            jsonErr.put("reqtype","tke_err");
                            printStream.println(jsonErr.toJSONString());
                            client_s.close();
                        }
                        ArrayList<String> PreFriendList = FriendModel.GetPreFriendList(jsonObject.getString("uuid"));
                        System.out.println(PreFriendList);
                        if(!PreFriendList.contains(jsonObject.getString("user"))){
                            JSONObject jsonErr = new JSONObject();
                            jsonErr.put("reqtype","fnf_err");
                            printStream.println(jsonErr.toJSONString());
                            client_s.close();
                        }

                        FriendModel.FriendAccept(jsonObject.getString("uuid"),jsonObject.getString("user"));

                        Finish(printStream,client_s);
                        break;
                    case "friendCancel":
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
                        FriendModel.FriendCancel(jsonObject.getString("uuid"),jsonObject.getString("user"));
                        Finish(printStream,client_s);
                        break;
                    case "friendDel":
                        if(!UserCheck(jsonObject.getString("token"),true)){
                            JSONObject jsonErr = new JSONObject();
                            jsonErr.put("reqtype","tke_err");
                            printStream.println(jsonErr.toJSONString());
                            client_s.close();
                        }
                        if(!FriendModel.GetFriendList(jsonObject.getString("token")).contains(jsonObject.getString("user"))){
                            JSONObject jsonErr = new JSONObject();
                            jsonErr.put("reqtype","unf_err");
                            printStream.println(jsonErr.toJSONString());
                            client_s.close();
                        }
                        FriendModel.FriendDel(jsonObject.getString("token"),jsonObject.getString("user"));
                        Finish(printStream,client_s);
                        break;
                    case "friendChat":
                        if(!UserCheck(jsonObject.getString("token"),true)){
                            JSONObject jsonErr = new JSONObject();
                            jsonErr.put("reqtype","tke_err");
                            printStream.println(jsonErr.toJSONString());
                            client_s.close();
                        }
                        if(!FriendModel.GetFriendList(jsonObject.getString("token")).contains(jsonObject.getString("user"))){
                            JSONObject jsonErr = new JSONObject();
                            jsonErr.put("reqtype","unf_err");
                            printStream.println(jsonErr.toJSONString());
                            client_s.close();
                        }
                        FriendModel.FriendChat(jsonObject.getString("token"),jsonObject.getString("user"),jsonObject.getString("msg"));
                        Finish(printStream,client_s);
                        break;
                    case "getAllFriendMsg":
                        if(!UserCheck(jsonObject.getString("token"),true)){
                            JSONObject jsonErr = new JSONObject();
                            jsonErr.put("reqtype","tke_err");
                            printStream.println(jsonErr.toJSONString());
                            client_s.close();
                        }
                        JSONObject et = new JSONObject();
                        et.put("reqtype","finish");
                        et.put("msgs",FriendModel.GetAllFriendMsg(jsonObject.getString("token")));
                        printStream.println(et.toJSONString());
                        client_s.close();
                        break;
                    case "friendList":
                        if(!UserCheck(jsonObject.getString("token"),true)){
                            JSONObject jsonErr = new JSONObject();
                            jsonErr.put("reqtype","tke_err");
                            printStream.println(jsonErr.toJSONString());
                            client_s.close();
                        }
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("reqtype","finish");
                        jsonObject1.put("friends",FriendModel.FriendList(jsonObject1.getString("token")));
                        printStream.println(jsonObject1.toJSONString());
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
