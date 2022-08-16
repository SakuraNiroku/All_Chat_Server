package cn.yincat.allchat.server.tools;

import cn.yincat.allchat.server.Var;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.internal.parser.Token;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

public class FriendModel {
    public static void FriendAdd(String uuid,String FriendName){
        String username = TokenModel.TokenUserTools(uuid,true);
        try {
            PreparedStatement preparedStatement = Var.mysqlVar.connection.prepareStatement("insert into PreFriend values (?,?)");
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,FriendName);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> GetPreFriendList(String uuid){
        String username = TokenModel.TokenUserTools(uuid,true);
        ArrayList<String> ret = new ArrayList<String>();
        try {
            PreparedStatement preparedStatement = Var.mysqlVar.connection.prepareStatement("select * from PreFriend where FriendName = ?");
            preparedStatement.setString(1,username);
            ResultSet r = preparedStatement.executeQuery();
            while(r.next()){
                ret.add(r.getString("FriendName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static ArrayList<String> GetFriendList(String uuid){
        String username = TokenModel.TokenUserTools(uuid,true);
        ArrayList<String> ret = new ArrayList<String>();
        try {
            PreparedStatement preparedStatement = Var.mysqlVar.connection.prepareStatement("select * from Friend where friendUser = ?");
            preparedStatement.setString(1,username);
            ResultSet r = preparedStatement.executeQuery();
            while(r.next()){
                ret.add(r.getString("user"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void FriendAccept(String uuid,String user){
        try {
            PreparedStatement preparedStatement2 = Var.mysqlVar.connection.prepareStatement("delete from PreFriend where Name = ? and FriendName = ?");
            preparedStatement2.setString(1,user);
            preparedStatement2.setString(2, TokenModel.TokenUserTools(uuid,true));
            preparedStatement2.executeUpdate();

            {
                PreparedStatement asaasawa = Var.mysqlVar.connection.prepareStatement("insert into Friend values (?,?)");
                asaasawa.setString(1,TokenModel.TokenUserTools(uuid,true));
                asaasawa.setString(2,user);
                asaasawa.executeUpdate();
            }

            {
                PreparedStatement asaasawa = Var.mysqlVar.connection.prepareStatement("insert into Friend values (?,?)");
                asaasawa.setString(1,user);
                asaasawa.setString(2,TokenModel.TokenUserTools(uuid,true));
                asaasawa.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void FriendCancel(String uuid,String user){
        try {
            PreparedStatement preparedStatement = Var.mysqlVar.connection.prepareStatement("delete from PreFriend where Name = ? and FriendName = ?");
            preparedStatement.setString(1,user);
            preparedStatement.setString(2,TokenModel.TokenUserTools(uuid,true));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void FriendDel(String uuid,String user){
        String username = TokenModel.TokenUserTools(uuid,true);
        try {
            {
                PreparedStatement preparedStatement = Var.mysqlVar.connection.prepareStatement("delete from Friend where user = ? and friendUser = ?");
                preparedStatement.setString(1,username);
                preparedStatement.setString(2,user);
                preparedStatement.executeUpdate();
            }
            {
                PreparedStatement preparedStatement = Var.mysqlVar.connection.prepareStatement("delete from Friend where user = ? and friendUser = ?");
                preparedStatement.setString(1,user);
                preparedStatement.setString(2,username);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void FriendChat(String uuid,String user,String msg){
        try{
            String username = TokenModel.TokenUserTools(uuid,true);
            PreparedStatement preparedStatement = Var.mysqlVar.connection.prepareStatement("insert into Message values (?,?,?,?)");
            preparedStatement.setInt(1, (int) System.currentTimeMillis());
            preparedStatement.setString(2,username);
            preparedStatement.setString(3,user);
            preparedStatement.setString(4,msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static JSONArray GetAllFriendMsg(String uuid){
        JSONArray jsonArray = new JSONArray();
        try{
            ResultSet resultSet = null;
            {
                PreparedStatement preparedStatement = Var.mysqlVar.connection.prepareStatement("select * from Message where User = ?");
                preparedStatement.setString(1, TokenModel.TokenUserTools(uuid, true));
                resultSet = preparedStatement.executeQuery();
            }
            {
                PreparedStatement preparedStatement = Var.mysqlVar.connection.prepareStatement("delete from Message where User = ?");
                preparedStatement.setString(1,TokenModel.TokenUserTools(uuid,true));
                preparedStatement.executeUpdate();
            }
            while (resultSet.next()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("date",resultSet.getInt("date"));
                jsonObject.put("user",resultSet.getString("Sender"));
                jsonObject.put("msg",resultSet.getString("Msg"));
                jsonArray.add(jsonObject);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static JSONArray FriendList(String uuid){
        String username = TokenModel.TokenUserTools(uuid,true);
        JSONArray objects = new JSONArray();
        try {
            PreparedStatement preparedStatement = Var.mysqlVar.connection.prepareStatement("select * from Friend where friendUser = ?");
            preparedStatement.setString(1,username);
            ResultSet r = preparedStatement.executeQuery();
            while(r.next()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user",r.getString("user"));
                objects.add(jsonObject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return objects;
    }
}
