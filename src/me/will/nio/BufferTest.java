package me.will.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * http://ifeve.com/buffers/
 */
public class BufferTest {


    public static void test() throws Exception {
        FileInputStream in = new FileInputStream(new File("/Users/duyisong/Downloads/download.pdf"));
        FileChannel channel = in.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        FileOutputStream out = new FileOutputStream(new File("/Users/duyisong/Downloads/download1.pdf"));
        FileChannel channel1 = out.getChannel();

        //从channel读数据到buffer，读入的数据从buffer的position开始往后排列，同时position往后移动。当position位于buffer最末位置，数据就不能被读入buffer了。
        //可以调用clear等方法，将position重置到buffer开始位置。
        int count = channel.read(buffer);
        System.out.println(String.format("read之后，读取的字节数：%s，position：%s，limit：%s", count, buffer.position(), buffer.limit()));

        //flip将position置为最开始位置，limit置为之前读数据时的位置，表示只能读这么多数据，为读取做准备
        buffer.flip();
        System.out.println(String.format("flip之后，position：%s，limit：%s", buffer.position(), buffer.limit()));

        //get(index)不会使得position移动
        buffer.get(1);
        System.out.println(String.format("get(index)之后，position：%s，limit：%s", buffer.position(), buffer.limit()));

        //get会使得position移动
        buffer.get();
        System.out.println(String.format("get()之后，position：%s，limit：%s", buffer.position(), buffer.limit()));

        //将buffer数据写到channel
        int writeCount = channel1.write(buffer);
        System.out.println(String.format("write之后，写入的字节数：%s，position：%s，limit：%s", writeCount, buffer.position(), buffer.limit()));
    }

    public static void main(String[] args) throws Exception {
        test();
    }
}
