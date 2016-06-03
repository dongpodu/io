package com.elisonwell.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.elisonwell.io.FileKit;

/**
 * http://www.ibm.com/developerworks/cn/education/java/j-nio/
 * @author duyisong
 * @createAt 2016年6月3日
 */
public class FileChannelTest {
	
	public static void save(FileInputStream in,FileOutputStream out){
		FileChannel inChannel = in.getChannel();
		FileChannel outChannel = out.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		try {
			int i = 0;
			while(true){
				buffer.clear();
				i = inChannel.read(buffer);
				if(i==-1){
					break;
				}
				buffer.flip();
				outChannel.write(buffer);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) throws Exception{
		File src = new File("/Users/duyisong/Documents/life/video/yiren.mp4");
		FileInputStream in = new FileInputStream(src);
		FileOutputStream out = new FileOutputStream(
				new File("/Users/duyisong/Downloads/nio/yiren.mp4"));
		long t = System.currentTimeMillis();
		FileKit.saveFile(src, "mp4");
		long t1 = System.currentTimeMillis();
		System.out.println(t1-t);
		
//		save(in, out);
//		long t2 = System.currentTimeMillis();
//		System.out.println(t2-t1);
	}
}
