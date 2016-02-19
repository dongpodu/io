package com.elisonwell.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;


public class SocketChannelTest {
	private static ServerSocket serverSock;

	public static void main(String[] args) throws UnknownHostException, IOException{
		serverSock = new ServerSocket(8000);
		while(true){
			java.net.Socket sock = serverSock.accept();
			SocketChannel channel = sock.getChannel();
			channel.configureBlocking(true);
			
			Selector selector = Selector.open();  
			SelectionKey key = channel.register(selector,  
					SelectionKey.OP_READ);
			key.selector();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			OutputStreamWriter writer = new OutputStreamWriter(sock.getOutputStream());
			String s = null; 
			while((s = reader.readLine())!=null){
				System.out.println(s);
				writer.write(s+"\n");
				writer.flush();
			}
		}
		
		
	}
}
