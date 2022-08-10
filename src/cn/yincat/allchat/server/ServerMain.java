package cn.yincat.allchat.server;

public class ServerMain {
    public static void main(String[] args) {
        System.out.println("Ready Start AllChat Server in 7600 Port!");
        System.out.println("Timer Start!");
        System.out.println("Starting AllChat Server!");
        long timer = System.currentTimeMillis();
        //启动
        Var.proMain.StartServer();

        long time = System.currentTimeMillis() - timer;
        System.out.println("Done Start! in "+ time+" ms");

        Var.socketMain.Run();
    }
}
