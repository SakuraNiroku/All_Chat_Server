import cn.yincat.allchat.server.ServerMain;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class TestClient {

    static Socket socket = null;
    static PrintStream printStream = null;
    static DataInputStream dataInputStream = null;
    static Scanner scanner = new Scanner(System.in);

    public static void GI(){
            if(socket.isClosed()){
                return;
            }
            String a = scanner.nextLine();
            printStream.println(a);
        try {
            Thread.sleep(3*1000);
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            socket = new Socket("127.0.0.1",7600);
            printStream = new PrintStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            new Thread(() -> GI()).start();
            String aa;
            while(true){
                if((aa = dataInputStream.readLine()) != null){
                    System.out.println(aa);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
