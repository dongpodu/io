package me.will.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

/**
 * socket测试
 *
 * @author duyisong
 * @createAt 2016年3月8日
 */
public class SocketTest {

    /**
     * 只接受单个客户端
     *
     * @throws IOException
     */
    public static void acceptOneClient() throws IOException {
        ServerSocket serverSock = new ServerSocket(8000);
        //接受客户端连接请求，accept方法阻塞
        java.net.Socket socket = serverSock.accept();
        System.out.println(String.format("接受客户端%s连接", socket.getPort()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String s;
        //read方法也为阻塞方法，直到读取到数据才往下走
        //while保证了服务端一直在等待从客户端读取数据，只要服务端socket不关闭，
        //服务端一直在读取客户端socket发送过来的数据
        while ((s = reader.readLine()) != null) {
            if (s.contains("eof")) {//遇到eof时就结束接收
                System.out.println("退出");
                break;
            }
            System.out.println(String.format("接受客户端%s数据：%s", socket.getPort(), s));
        }
    }

    /**
     * 接受多个客户端，该方法有问题，因为在同一刻只能处理单个用户，下一个客户连接并发送信息时，只能等到
     * 上一个处理完了才能进行
     *
     * @throws IOException
     */
    public static void acceptManyClient() throws IOException {
        ServerSocket serverSock = new ServerSocket(8000);
        System.out.println("新建服务端socket");
        while (true) {
            java.net.Socket socket = serverSock.accept();
            System.out.println(String.format("接受客户端%s连接", socket.getPort()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String s;
            while ((s = reader.readLine()) != null) {
                if (s.contains("eof")) {
                    System.out.println("退出");
                    break;
                }
                System.out.println(String.format("接受客户端%s数据：%s", socket.getPort(), s));
            }
        }
    }

    /**
     * acceptManyClient方法的改进
     *
     * @throws IOException
     */
    public static void acceptManyCustom1() throws IOException {
        ServerSocket serverSock = new ServerSocket(8000);
        System.out.println("新建服务端socket");
        while (true) {
            java.net.Socket socket = serverSock.accept();
            System.out.println(String.format("接受客户端%s连接", socket.getPort()));
            new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String s;
                    while ((s = reader.readLine()) != null) {
                        if (s.contains("eof")) {
                            System.out.println("退出");
                            break;
                        }
                        System.out.println(String.format("接受客户端%s数据：%s", socket.getPort(), s));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static void acceptManyCustom2() throws IOException {
        ServerSocket serverSock = new ServerSocket(8000);
        while (true) {
            java.net.Socket socket = serverSock.accept();
            System.out.println("接受客户端socket");
            new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String s = reader.readLine(); //此处堵塞，一但客户端发送一次数据，此处即可读取成功，且只读取一次，线程即被销毁，客户端再发送数据时，服务端已接受不到了
                    System.out.println("接受客户端数据：" + s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }


    public static void main(String[] args) throws IOException {
//		acceptOneClient();
//        acceptManyClient();
        acceptManyCustom1();
//        acceptManyCustom2();
    }
}
