package com.elisonwell.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class SocketChannelTest {

	/**
	 * 传统io阻塞方式
	 * @throws Exception
	 */
	public static void test() throws Exception {
		ServerSocket serverSock = new ServerSocket(8000);
		while(true){
			/*
			accept方法阻塞，直到请求到达
			 */
			java.net.Socket sock = serverSock.accept();
			BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			OutputStreamWriter writer = new OutputStreamWriter(sock.getOutputStream());
			System.out.println("----------------------------");
			String s;
			while(true){
				s = reader.readLine();
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
		}
	}

	public static void test1() throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(8001));
		while (true){
			/*
			而阻塞模式下，accept方法会被阻塞，循环会被卡住，直到请求到达：
			When the accept() method returns, it returns a SocketChannel with an incoming connection. Thus, the accept() method blocks until an incoming connection arrives
			 */
			SocketChannel socketChannel = serverSocketChannel.accept();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			int count = socketChannel.read(buffer);
			System.out.println("----------------------------------------");
			System.out.println("读取字节数："+count);
			System.out.println("读取字符："+ new String(buffer.array()));
		}
	}

	public static void test2() throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(8002));
		serverSocketChannel.configureBlocking(false);
		while (true){
			/*
			非阻塞模式下，accept方法会立即返回，如果没有请求到达，会立即返回null，循环会一直执行
			 */
			SocketChannel socketChannel = serverSocketChannel.accept();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			if(socketChannel!=null){
				int count = socketChannel.read(buffer);
				System.out.println("----------------------------------------");
				System.out.println("读取字节数："+count);
				System.out.println("读取字符："+ new String(buffer.array()));
			}
		}
	}

	public static void test3() throws IOException {

		//开启单个线程处理任务
		Selector selector = Selector.open();
		Executor pool = Executors.newSingleThreadExecutor();
		pool.execute(()->{
			while (true){
				try {
					int select = selector.select();
					System.out.println("已准备的channel数："+select);
					if(select==0){
						continue;
					}
					Iterator<SelectionKey> it = selector.selectedKeys().iterator();
					while (it.hasNext()){
						SelectionKey key = it.next();
						SocketChannel socketChannel = (SocketChannel)key.channel();
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						int count = socketChannel.read(buffer);
						System.out.println("----------------------------------------");
						System.out.println("读取字节数："+count);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		//主线程监听端口
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(8003));
		serverSocketChannel.configureBlocking(false);
		while (true){
			SocketChannel socketChannel = serverSocketChannel.accept();
			if(socketChannel!=null){
				socketChannel.configureBlocking(false);
				//由于selector阻塞在select方法上，需要wakeup后才能register成功，否则register会被阻塞
				selector.wakeup();
				socketChannel.register(selector,SelectionKey.OP_READ);
				System.out.println("注册成功");
			}
		}
	}


	public static void main(String[] args) throws Exception{
//		test();
//		test1();
//		test2();
		test3();
	}
}
