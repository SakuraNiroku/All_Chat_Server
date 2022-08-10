package cn.yincat.allchat.server.tools;

import cn.yincat.allchat.server.Var;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

public class FriendModel {
    public static void FriendAdd(String uuid,String FriendName){
        String username = TokenModel.TokenUserTools(uuid,true);
        try {
            PreparedStatement preparedStatement = Var.mysqlVar.connection.prepareStatement("insert into PreFriend values (?,?)");
            preparedStatement.setString(1,md5Hex(username));
            preparedStatement.setString(2,md5Hex(FriendName));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
