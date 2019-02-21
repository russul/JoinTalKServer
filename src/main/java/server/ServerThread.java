package server;

import ui.ServerTestUI;
import ui.ServerUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author: create by kevinYang
 * @version: v1.0
 * @description: server
 * @date:2019/2/20 0020
 */
public class ServerThread implements Runnable {


    //获取的客户端Socket
    Socket s = null;
    //获取的服务器ServerSocket
    ServerSocket ss = null;
    //获取的客户端IP
    String ip = null;
    //获取的客户端端口
    int port = 0;
    String username;
    //组合客户端的ip和端口字符串得到uid字符串
    String uid = null;

    //静态ArrayList存储所有uid，uid由ip和端口字符串拼接而成
    static ArrayList<String> uid_arr = new ArrayList<String>();
    //静态HashMap存储所有uid, Socket对象组成的对(保存Socket是为了服务器能获取向客户端的输出流)
    static HashMap<String, ServerThread> hm = new HashMap<String, ServerThread>();


    public ServerThread(Socket s, ServerSocket ss, String ip, int port) {
        this.s = s;
        this.ss = ss;
        this.ip = ip;
        this.port = port;

        uid = ip + ":" + port;
    }


    public ServerThread(ServerSocket ss) {
        this.ss = ss;
    }

    public void run() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        InputStream in = null;
        OutputStream out = null;
        //准备缓冲区
        byte[] buf = new byte[1024];
        int len = 0;
        try {

            //获取输入流
            in = s.getInputStream();
            //获取输出流
            out = s.getOutputStream();
            String welcome = "连接成功/";
            out.write(welcome.getBytes());


            len = in.read(buf);

            String username = new String(buf, 0, len);

            uid = uid + "/" + username;

            System.out.println(uid);
            //将当前客户端uid存入ArrayList
            uid_arr.add(uid);
            //将当前uid和服务端的Socket对存入HashMap
            hm.put(uid, this);
            //控制台打印客户端IP和端口
            System.out.println("Client connected: " + uid);

            //时间显示格式


//
            //向当前客户端传输连接成功信息
//                String welcome = sdf.format(new Date()) + "\n成功连接服务器...\n服务器IP: " + ss.getInetAddress().getLocalHost().getHostAddress() + ", 端口: 30000\n客户端IP: " + ip + ", 端口: " + port + "\n";


        } catch (IOException e) {
            e.printStackTrace();
        }

        //持续监听并转发客户端消息

        while (true) {
            try {


                len = in.read(buf);
                String msg = new String(buf, 0, len);

                System.out.println(msg);
                //消息类型：退出或者聊天
                String type = msg.substring(0, msg.indexOf("/"));
                //消息本体：空或者聊天内容
                String content = msg.substring(msg.indexOf("/") + 1);

                String username = content.substring(content.indexOf("/") + 1);


                //根据消息类型分别处理
                //客户端要退出
                if (type.equals("Exit")) {
                    //更新ArrayList和HashMap, 删除退出的uid和线程
                    uid_arr.remove(uid_arr.indexOf(uid));
                    hm.remove(uid);

                    //广播更新在线名单
                    updateOnlineList(out);
                    //控制台打印客户端IP和端口
                    System.out.println("Client exited: " + uid);


                    int rows = Server.sUI.jtbOnline.getRowCount();
//                    int cols = Server.sUI.jtbOnline.getColumnCount();

//                    清除该用户在服务器客户端界面上的在线列表记录
                    DefaultTableModel model = (DefaultTableModel) Server.sUI.jtbOnline.getModel();
                    String val = null;
                    for (int i = 0; i < rows; i++) {
                        val = (String) model.getValueAt(i, 0);
                        String name = val.substring(val.lastIndexOf(":") + 1);
//                        System.out.println(name);
                        String uid_name = uid.substring(uid.indexOf("/") + 1);
//                        System.out.println(uid_name);
                        if (name.equals(uid_name)) {
                            model.removeRow(i);
                            break;
                        }
                    }
                    //结束循环，结束该服务线程
                    break;
                }
                //客户端要群聊天
                else if (type.equals("WeChat")) {
//                    //提取收信者地址
//                    String[] receiver_arr = content.substring(0, content.indexOf("/")).split(",");
                    //提取聊天内容
                    String word = msg.substring(msg.indexOf("/") + 1);

                    word = word.replace("/", ":");

                    System.out.println("in:" + word);
//                    Server.sUI.jtaInfo.setText("消息" + "\n");
                    Server.sUI.jtaInfo.append(word + "\n");
                    //向在线用户广播消息

                    chatOnlineList(out, uid, uid_arr, word);

                } else if (type.equals("Enter")) {
                    System.out.println(username + "进入聊天室");

//                    ServerUI serverUI = new ServerUI();
//                    serverUI.setVisible(true);
//                    serverUI.jtaOnline.append(sdf.format(new Date()) + ":" + content);

                    //提取在线列表的数据模型
                    DefaultTableModel tbm = (DefaultTableModel) Server.sUI.jtbOnline.getModel();
//                    //清除在线名单列表
//                    tbm.setRowCount(0);
                    //更新在线名单
//                    String[] onlinelist = content.split(",");
                    //逐一添加当前在线者
//                    for(String member : onlinelist)
//                    {
//                        String[] tmp = new String[3];
//                        //如果是自己则不在名单中显示
//                        if(member.equals(InetAddress.getLocalHost().getHostAddress() + ":" + s.getLocalPort()))
//                            continue;
//                        tmp[0] = "";
//                        tmp[1] = member.substring(0, member.indexOf(":"));
//                        tmp[2] = member.substring(member.indexOf(":") + 1);
//                        //添加当前在线者之一
//                        tbm.addRow(tmp);
//                    }
                    String[] rowData = new String[]{sdf.format(new Date()) + ":" + username};
                    tbm.addRow(rowData);
                    //提取在线列表的渲染模型
                    DefaultTableCellRenderer tbr = new DefaultTableCellRenderer();
                    //表格数据居中显示
                    tbr.setHorizontalAlignment(JLabel.CENTER);
                    Server.sUI.jtbOnline.setDefaultRenderer(Object.class, tbr);

//                  当有新用户进入时进行在线用户广播
                    updateOnlineList(out);

                } else if (type.equals("getIP")) {
                    for (String uid_name : uid_arr
                            ) {
                        if (uid_name.substring(uid_name.lastIndexOf("/") + 1).equals(content)) {
                            String address = uid_name.substring(0, uid_name.lastIndexOf("/"));
//                            System.out.println(address);
                            out.write(("getIP/" + address).getBytes());
                        }
                    }
                } else if (type.equals("SingleChat")) {

                    String[] msgs = msg.split("/");
                    String receiver = msgs[1] + "/" + msgs[2];
                    String word = msgs[3] + ":" + msgs[4];
                    System.out.println(receiver);
                    System.out.println(word);
                    Server.sUI.jtaInfo.append(word + "\n");
                    OutputStream outputStream = hm.get(receiver).s.getOutputStream();
                    outputStream.write(("SingleChat/" + word).getBytes());

                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    //向所有已连接的客户端更新在线名单
    public void updateOnlineList(OutputStream out) throws Exception {
        for (String tmp_uid : uid_arr) {

            //获取广播收听者的输出流
            out = hm.get(tmp_uid).s.getOutputStream();
            //将当前在线名单以逗号为分割组合成长字符串一次传送
            StringBuilder sb = new StringBuilder("OnlineListUpdate/");
            for (String member : uid_arr) {

                sb.append(member);
                //以逗号分隔uid，除了最后一个
                if (uid_arr.indexOf(member) != uid_arr.size() - 1)
                    sb.append(",");
            }
            out.write(sb.toString().getBytes());
        }
    }

    //向指定的客户端发送聊天消息
    public void chatOnlineList(OutputStream out, String uid, ArrayList<String> receiver_arr, String word) throws Exception {
        for (String tmp_uid : receiver_arr) {
            //获取广播收听者的输出流
            out = hm.get(tmp_uid).s.getOutputStream();
            //发送聊天信息
            String msg = "WeChat/" + uid + ":" + word;
            System.out.println("out:" + msg);
            out.write(msg.getBytes());
        }
    }


}
