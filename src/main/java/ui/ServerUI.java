package ui;

import server.Server;
import server.ServerThread;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: create by kevinYang
 * @version: v1.0
 * @description: ui
 * @date:2019/2/20 0020
 */
public class ServerUI extends JFrame {

    //窗口宽度
    public final int WIDTH = 600;
    //窗口高度
    public final int HEIGHT = 700;

    JButton btnStart = new JButton("启动服务器");
    JButton btnEnd = new JButton("关闭服务器");

    public JTextArea jtaInfo = new JTextArea("消息" + "\n");
    public JTextArea jtaOnline = new JTextArea("在线用户" + "\n");

    //当前在线列表的列标题
    String[] colTitles = {"在线列表"};
    //当前在线列表的数据
    String[][] rowData = null;
    //创建当前在线列表
    public JTable jtbOnline = new JTable
            (
                    new DefaultTableModel(rowData, colTitles)
                    {
                        //表格不可编辑，只可显示
                        @Override
                        public boolean isCellEditable(int row, int column)
                        {
                            return false;
                        }
                    }
            );

    //创建聊天消息框的滚动窗
    JScrollPane jspInfo = new JScrollPane(jtaInfo);

    //创建当前在线列表的滚动窗
    JScrollPane jspOnline = new JScrollPane(jtbOnline);


    public ServerUI() throws HeadlessException {

        //标题
        setTitle("聊天室");
        //大小
        setSize(WIDTH, HEIGHT);
        //不可缩放
        setResizable(false);
        //设置布局:不适用默认布局，完全自定义
        setLayout(null);


        //
        btnStart.setBounds(60, 50, 200, 50);
        btnEnd.setBounds(340, 50, 200, 50);


//        jtaInfo.setBounds(60, 150, 200, 500);
//        jtaOnline.setBounds(340, 150, 200, 500);


        this.add(btnStart);
        this.add(btnEnd);


        //聊天消息框自动换行
        jtaInfo.setLineWrap(true);
        //聊天框不可编辑，只用来显示
        jtaInfo.setEditable(false);
        //设置聊天框字体
        jtaInfo.setFont(new Font("楷体", Font.BOLD, 12));

        //设置滚动窗的水平滚动条属性:不出现
        jspInfo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //设置滚动窗的垂直滚动条属性:需要时自动出现
        jspInfo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //设置滚动窗大小和位置
        jspInfo.setBounds(60, 150, 200, 500);
        //添加聊天窗口的滚动窗
        this.add(jspInfo);

        jtaOnline.setFont(new Font("楷体", Font.BOLD, 12));


        //设置滚动窗的水平滚动条属性:不出现
        jspOnline.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //设置滚动窗的垂直滚动条属性:需要时自动出现
        jspOnline.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //设置当前在线列表滚动窗大小和位置
        jspOnline.setBounds(340, 150, 200, 500);
        //添加当前在线列表
        this.add(jspOnline);


        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Server.ss = new ServerSocket(30000);
                    System.out.println("Server online: " + Server.ss.getInetAddress().getLocalHost().getHostAddress() + ":" + 30000);

                    //时间显示格式
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                    ServerUI.this.jtaInfo.append(sdf.format(new Date()) + ":" + "启动成功" + "\n");
//                    ServerThread serverThread = new ServerThread(Server.ss);
//
//
//                    new Thread(serverThread).start();

//                    while (true){
//
//                        System.out.println(Server.ss);
//                        Socket s = Server.ss.accept();
//                        String ip = s.getInetAddress().getHostAddress();
//                        int port  = s.getPort();
//                        ServerThread serverThread = new ServerThread(s,Server.ss,ip,port);
//                        new Thread(serverThread).start();
//
//                    }


                } catch (IOException e1) {
                    e1.printStackTrace();
                }


            }
        });

        btnEnd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    Socket socket = new Socket(InetAddress.getLocalHost(), 30000);
                    OutputStream out = socket.getOutputStream();
                    out.write("Exit".getBytes());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }
}
