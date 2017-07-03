package com.tzg.tool.kit.test.net;

import java.net.*;
import java.io.*;

public class HttpSimulator {
    private Socket socket;
    private int    port    = 80;
    private String host    = "localhost";
    private String request = "";         // HTTP请求消息
    private boolean isPost, isHead;

    public void run() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                if (!readHostAndPort(reader))
                    break;
                readHttpRequest(reader);
                sendHttpRequest();
                readHttpResponse(reader);
            } catch (Exception e) {
                System.out.println("err:" + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new HttpSimulator().run();
    }

    private boolean readHostAndPort(BufferedReader consoleReader) throws Exception {
        System.out.print("host:port>");
        String[] ss = null;
        String s = consoleReader.readLine();
        if (s.equals("q"))
            return false;

        ss = s.split("[:]");
        if (!ss[0].equals(""))
            host = ss[0];
        if (ss.length > 1)
            port = Integer.parseInt(ss[1]);
        System.out.println(host + ":" + String.valueOf(port));
        return true;
    }

    private void readHttpRequest(BufferedReader consoleReader) throws Exception {
        System.out.println("请输入HTTP请求:");
        String s = consoleReader.readLine();
        request = s + "\r\n";
        boolean isPost = s.substring(0, 4).equals("POST");
        boolean isHead = s.substring(0, 4).equals("HEAD");
        while (!(s = consoleReader.readLine()).equals(""))
            request = request + s + "\r\n";
        request = request + "\r\n";
        if (isPost) {
            System.out.println("请输入POST方法的内容:");
            s = consoleReader.readLine();
            request = request + s;
        }
    }

    private void sendHttpRequest() throws Exception {
        socket = new Socket();
        socket.setSoTimeout(10 * 1000);
        System.out.println("正在连接服务器");
        socket.connect(new InetSocketAddress(host, port), 10 * 1000);
        System.out.println("服务器连接成功！");
        OutputStream out = socket.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out);
        writer.write(request);
        writer.flush();
    }

    private void readHttpResponse(BufferedReader consoleReader) {
        String s = "";
        try {
            InputStream in = socket.getInputStream();
            InputStreamReader inReader = new InputStreamReader(in);
            BufferedReader socketReader = new BufferedReader(inReader);
            System.out.println("---------HTTP头---------");
            boolean b = true; // true: 未读取消息头 false: 已经读取消息头
            while ((s = socketReader.readLine()) != null) {
                if (s.equals("") && b == true && !isHead) {
                    System.out.println("------------------------");
                    b = false;
                    System.out.print("是否显示HTTP的内容(Y/N):");
                    String choice = consoleReader.readLine();
                    if (choice.equalsIgnoreCase("y")) {
                        System.out.println("---------HTTP内容---------");
                        continue;
                    } else
                        break;
                } else
                    System.out.println(s);
            }
        } catch (Exception e) {
            System.out.println("err:" + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
        System.out.println("------------------------");
    }
}
