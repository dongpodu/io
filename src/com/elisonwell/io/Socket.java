package com.elisonwell.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.UnknownHostException;


public class Socket {
	private static ServerSocket serverSock;

	public static void main(String[] args) throws UnknownHostException, IOException{
		serverSock = new ServerSocket(8000);
		while(true){
			java.net.Socket sock = serverSock.accept();
			sock.getChannel();
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
