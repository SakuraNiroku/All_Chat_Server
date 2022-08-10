package cn.yincat.allchat.server;

import cn.yincat.allchat.server.mysql.MysqlVar;
import cn.yincat.allchat.server.pro.ProMain;
import cn.yincat.allchat.server.socket.SocketMain;
import cn.yincat.allchat.server.socket.SocketVar;

public class Var {
    public static ProMain proMain = new ProMain();
    public static SocketVar socketVar = new SocketVar();
    public static MysqlVar mysqlVar = new MysqlVar();
    public static SocketMain socketMain = new SocketMain();

}
