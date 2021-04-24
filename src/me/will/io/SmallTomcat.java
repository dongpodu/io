package me.will.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SmallTomcat {

    /**
     * 主线程监听端口，收到请求后立马将获得的socket丢给工作线程处理，主线程继续返回监听端口
     * 这种模式缺点是：当并发很多时，开启的工作线程也会很多
     * @throws IOException
     */
    public static void test() throws IOException {
        ServerSocket serverSocket = new ServerSocket(9000);
        Executor executor = Executors.newCachedThreadPool();
        while (true){
            Socket socket = serverSocket.accept();
            System.out.println("#####获得连接#######");

            executor.execute(()->{
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
                    System.out.println("----------------------------");
                    String s;
                    while(true){
                        s = reader.readLine();
                        if(s.indexOf("?block=true")!=-1){
                            System.out.println("sleep");
                            Thread.sleep(60*1000);
                        }
                        if(s!=null && s.length()>0){
                            System.out.println(s);
                        }else{
                            break;
                        }
                    }
                    String result = "HTTP/1.1 200 OK\n" +
                            "Cache-Control: private\n" +
                            "Connection: Keep-Alive\n" +
                            "Content-Type: text/html;charset=utf-8\n" +
                            "Date: Wed, 26 Sep 2018 03:07:50 GMT\n" +
                            "Content-Length: 5\n" +
                            "\n" +
                            "helloddd";
                    writer.write(result);
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void main(String[] args) throws IOException {
        test();
    }


}
