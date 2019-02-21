package server;

import ui.ServerTestUI;
import ui.ServerUI;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: create by kevinYang
 * @version: v1.0
 * @description: server
 * @date:2019/2/20 0020
 */
public class Server {

    public static ServerSocket ss ;

    public static ServerUI sUI;

    public static ServerTestUI stUI;


    public static void main(String[] args) throws IOException, InterruptedException {


//        System.out.println("Server online: " + ss.getInetAddress().getLocalHost().getHostAddress() + ":" + 30000);

        sUI = new ServerUI();

        //        //窗口关闭键无效，必须通过退出键退出客户端以便善后
//        ef.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //获取本机屏幕横向分辨率
        int w = Toolkit.getDefaultToolkit().getScreenSize().width;
        //获取本机屏幕纵向分辨率
        int h = Toolkit.getDefaultToolkit().getScreenSize().height;
        //将窗口置中
        sUI.setLocation((w - sUI.WIDTH) / 2, (h - sUI.HEIGHT) / 2);
        //设置客户端窗口为可见
        sUI.setVisible(true);


        //时间显示格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");



//      服务器一直处于启动状态
        while (true){
            Thread.sleep(1000);
            if (ss!=null){
//                System.out.println(Server.ss);
                Socket s = Server.ss.accept();
                String ip = s.getInetAddress().getHostAddress();
                int port  = s.getPort();
//              为每个连接成功的Socket,分配一个服务器线程
                ServerThread serverThread = new ServerThread(s,Server.ss,ip,port);
                new Thread(serverThread).start();

            }


        }










//
//        while(true){
//            //该方法获取连接的Socket,阻塞到连接建立位置
//            Socket s = ss.accept();
//
//            ServerThread serverThread = new ServerThread(s,ss,s.getInetAddress().getHostAddress(),s.getPort());
//
//            new Thread(serverThread).start();
//        }

    }
}
