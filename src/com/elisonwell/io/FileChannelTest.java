package com.elisonwell.io;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelTest {
	private static FileInputStream in;

	public static void main(String[] args) throws Exception{
		in = new FileInputStream(new File("/Users/duyisong/Downloads/test.txt"));
		FileChannel channel = in.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(48);
		int i = channel.read(buffer);
		while(i!=-1){
			System.out.println(i);
			buffer.flip();
			  
			while(buffer.hasRemaining()){  
				System.out.print((char) buffer.get());  
			}  
			  
			buffer.clear();  
			i = channel.read(buffer);
			
		}
		in.close();
	}
}
