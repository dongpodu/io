package me.will.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * http://www.ibm.com/developerworks/cn/education/java/j-nio/
 *
 * @author duyisong
 * @createAt 2016年6月3日
 */
public class FileChannelTest {

    /**
     * 将数据从channel读到buffer
     *
     * @throws Exception
     */
    public static void testRead() throws Exception {
        String path = "/Users/duyisong/Downloads/download.pdf";
        /*
         * 不能用fileOutputStream，因为用fileOutputStream获取的channel调用read方法时，会报NonReadableChannelException
         */
        FileInputStream in = new FileInputStream(new File(path));
        FileChannel channel = in.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (true) {
            int count = channel.read(buffer);
            System.out.println("字节数：" + count + "，位置：" + buffer.position());
            if (count == -1) {
                break;
            }
            buffer.clear();
        }
    }

    /**
     * 复制文件
     *
     * @throws Exception
     */
    public static void testTransfer() throws Exception {
        FileInputStream in = new FileInputStream(new File("/Users/bilibili/Downloads/download.pdf"));
        FileChannel channel = in.getChannel();

        FileOutputStream out = new FileOutputStream(new File("/Users/bilibili/Downloads/download1.pdf"));

        long bytes = 10000;
        long position = 0;
        while (true) {
            long realTransferBytes = channel.transferTo(position, bytes, out.getChannel());
            if (realTransferBytes == 0) {
                break;
            }
            position += realTransferBytes;
        }
    }

    /**
     * 将数据从buffer写到channel
     *
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


    public static void main(String[] args) throws Exception {
        testTransfer();
    }
}
