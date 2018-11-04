package com.elisonwell.io.input;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

/**
 * buffered内含byte数组，每次读取时都是从该数组中读取，如果数组中没有数据了，则会调用inputStream的read(byte[],pos,length)读取数据，
 * 将读取到的数据填充到byte数组中，下次再读取时，直接从数组中拿。
 * Created by duyisong on 04/11/2018.
 */
public class BufferedInputSteamTest {

    public void test() throws Exception {
        String str = new String("hello, i am will, nice to meet you!");
        byte[] data = str.getBytes("utf-8");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        BufferedInputStream buffer = new BufferedInputStream(inputStream,1024);
        byte[] bytes = new byte[4];
        buffer.read(bytes);
        System.out.println(bytes.length);
    }

    public static void main(String[] args) throws Exception {
        new BufferedInputSteamTest().test();
    }
}
