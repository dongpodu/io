package me.will.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * socket测试
 * <p>
 * 详情请见：http://haohaoxuexi.iteye.com/blog/1979837
 *
 * @author duyisong
 * @createAt 2016年3月8日
 */
public class SocketTest {
    private static ServerSocket serverSock;

    /**
     * 只接受单个客户端
     *
     * @throws IOException
     */
    public static void acceptSingleCustom() throws IOException {
        serverSock = new ServerSocket(8000);
        System.out.println("新建服务端socket");
        //接受客户端socket请求，accept方法阻塞，直到接收到客户端socket才继续往下走
        java.net.Socket sock = serverSock.accept();
        System.out.println("接受客户端socket");
        BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        String s = null;
        //read方法也为阻塞方法，直到读取到数据才往下走
        //while保证了服务端一直在等待从客户端读取数据，只要服务端socket不关闭，
        //服务端一直在读取客户端socket发送过来的数据
        while ((s = reader.readLine()) != null) {
            if (s.indexOf("eof") != -1) {//遇到eof时就结束接收
                System.out.println("退出");
                break;
            }
            System.out.println("接受客户端数据：" + s);
        }
    }

    /**
     * 接受多个客户端，该方法有问题，因为在同一刻只能处理单个用户，下一个客户连接并发送信息时，只能等到
     * 上一个处理完了才能进行
     *
     * @throws IOException
     */
    public static void acceptMutiplCustom() throws IOException {

        serverSock = new ServerSocket(8000);
        System.out.println("新建服务端socket");
        while (true) {
            java.net.Socket sock = serverSock.accept();
            System.out.println("接受客户端socket");
            BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String s = null;
            while ((s = reader.readLine()) != null) {
                if (s.indexOf("eof") != -1) {
                    System.out.println("退出");
                    break;
                }
                System.out.println("接受客户端数据：" + s);
            }
        }
    }

    /**
     * acceptMutiplCustom方法的改进
     *
     * @throws IOException
     */
    public static void acceptMutiplCustom1() throws IOException {

        serverSock = new ServerSocket(8000);
        System.out.println("新建服务端socket");
        while (true) {
            java.net.Socket socket = serverSock.accept();
            System.out.println("接受客户端socket");
            new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String s;
                    while ((s = reader.readLine()) != null) {
                        if (s.contains("eof")) {
                            System.out.println("退出");
                            break;
                        }
                        System.out.println("接受客户端数据：" + s);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static void test() {
        Socket socket1 = new Socket();
    }


    public static void main(String[] args) throws IOException {
//		acceptSingleCustom();
//		acceptMutiplCustom();
        acceptMutiplCustom1();
    }
}
