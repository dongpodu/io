package me.will.io.input;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by duyisong on 31/10/2018.
 */
public class ByteArrayInputStreamTest {

    public void test() throws IOException {
        String str = new String("你");
        byte[] data = str.getBytes("utf-8");
        for(byte b:data){
            System.out.println(b);
        }
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        int result;
        //result为读取的结果，不是读取的字节数
        while ((result=in.read())!=-1){
            System.out.println(result);
        }

        //输入流已被上面的代码读完，故下面的代码无法再读取到数据
        byte[] bytes = new byte[100];
        for(byte b:bytes){
            System.out.println(b);
        }
    }

    public static void main(String[] args) throws IOException {
        new ByteArrayInputStreamTest().test();
    }
}
