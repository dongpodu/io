package com.elisonwell.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * http://www.ibm.com/developerworks/cn/education/java/j-nio/
 * @author duyisong
 * @createAt 2016年6月3日
 */
public class FileChannelTest {

	/**
	 * 将数据从channel读到buffer
	 * @throws Exception
	 */
	public static void testRead() throws Exception {
		String path = "D:\\work\\test.jpg";
		/*
		 * 不能用fileOutputStream，因为用fileOutputStream获取的channel调用read方法时，会报NonReadableChannelException
		 */
		FileInputStream in = new FileInputStream(new File(path));
		FileChannel channel = in.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		while(true){
			int count = channel.read(buffer);
			System.out.println("字节数："+count+"，位置："+buffer.position());
			if(count==-1){
				break;
			}
			/*
			buffer内部维护了position属性，每次将数据读到buffer时，都是将数据读到buffer内以position开始依次向后的位置，且使得position后移，
			所以如果没有buffer.flip()，那么在第一次读完后，就无法再读取数据了。
			 */
			buffer.flip();
		}
	}

	/**
	 * 将数据从buffer写到channel
	 * @throws Exception
	 */
	public static void testWrite() throws Exception {
		String test = "New String to write to file";
		/*
		 * 不能用fileInputStream，因为用fileInputStream获取的channel调用write方法时，会报NonWritableChannelException
		 */
		FileOutputStream out = new FileOutputStream(new File("D:\\work\\test.txt"));
		FileChannel outChannel = out.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(84);
		buffer.clear();
		buffer.put(test.getBytes());
		buffer.flip();
		while (buffer.hasRemaining()) {
			outChannel.write(buffer);
		}
	}


	public static void main(String[] args) throws Exception{
		testRead();
	}
}
