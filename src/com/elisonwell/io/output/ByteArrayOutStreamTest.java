package com.elisonwell.io.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by duyisong on 31/10/2018.
 */
public class ByteArrayOutStreamTest {

    public void test() throws IOException {
        String str = new String("你");
        byte[] data = str.getBytes("utf-8");
        for(byte b:data){
            System.out.println(b);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        //将data写入到流中
        out.write(data);
        //将流转换为字节数组
        byte[] newData = out.toByteArray();
        for(byte b:newData){
            System.out.println(b);
        }
    }

    public static void main(String[] args) throws IOException {
        new ByteArrayOutStreamTest().test();
    }
}
