package cn.yincat.allchat.server.pro;

import cn.yincat.allchat.server.Var;
import cn.yincat.allchat.server.mysql.PassMy;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ProMain {
    public void StartServer(){
        //数据库配置
        /*1、数据库Driver导入*/
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
        /*2、数据库连接配置*/
        Var.mysqlVar.url = PassMy.url; //防偷看 awa
        Var.mysqlVar.database = "allchat"+"?autoReconnect=true";
        Var.mysqlVar.user = PassMy.d;
        Var.mysqlVar.password = PassMy.pass;
        /*3、连接数据库*/
        try {
            Var.mysqlVar.connection = DriverManager.getConnection(Var.mysqlVar.url+Var.mysqlVar.database,Var.mysqlVar.user,Var.mysqlVar.password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("数据库连接完成！");

        //ServerSocket配置
        /*1、初始化ServerSocket*/
        try {
            Var.socketVar.serverSocket = new ServerSocket(7600);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("ServerSocket初始化完成！");
    }
}
