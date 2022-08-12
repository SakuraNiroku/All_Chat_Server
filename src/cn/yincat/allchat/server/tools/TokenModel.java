package cn.yincat.allchat.server.tools;

import cn.yincat.allchat.server.Var;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenModel {
    public static String TokenUserTools(String string,boolean tokenToUser){
        if(tokenToUser){
            try {

                ResultSet a = Var.mysqlVar.connection.createStatement().executeQuery("select * from user where uuid = '"+string+"'");
                if(a.next()){
                    //System.out.println(a.getString(1));
                    return a.getString(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            try {
                ResultSet a = Var.mysqlVar.connection.createStatement().executeQuery("select * from user where User = '"+string+"'");
                if(a.next()){
                    return a.getString("uuid");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "err";
    }
}
