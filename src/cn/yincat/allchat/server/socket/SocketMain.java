package cn.yincat.allchat.server.socket;

import cn.yincat.allchat.server.Var;

import java.io.IOException;
import java.net.Socket;

import static cn.yincat.allchat.server.Var.socketVar;

public class SocketMain {
    public void Run(){
        while(true){
            try {
                Socket socketa = socketVar.serverSocket.accept();
                new SocketThread(socketa).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
