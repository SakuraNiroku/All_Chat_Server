package cn.yincat.allchat.server.tools;

import cn.yincat.allchat.server.Var;

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
            preparedStatement.setString(2,md5Hex(FriendName));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> GetPreFriendList(String uuid){
        String username = TokenModel.TokenUserTools(uuid,true);
        ArrayList<String> ret = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = Var.mysqlVar.connection.prepareStatement("select FriendName from PreFriend where Name = ?");
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
}
