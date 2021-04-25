package me.will.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 1、一个selector可以处理多个通道
 * 2、与Selector一起使用时，Channel必须处于非阻塞模式下
 * 3、有四种事件类型：Connect、Accept、Read、Write，不同的channel支持的事件类型可能不同，但都在这四个事件类型之内
 * 4、当一个channel对多个事件感兴趣时，可以在注册时传递多个事件的或运算结果，如SelectionKey.OP_ACCEPT | SelectionKey.OP_CONNECT
 * 5、判断事件是否就绪可以使用：A、selectionKey.readyOps();  B、selectionKey.isAcceptable()...  C、selector.select()
 * 6、
 */
public class SelectorTest {

    public static void test() throws IOException {
        Selector selector = Selector.open();

        ServerSocketChannel channel8081 = ServerSocketChannel.open();
        channel8081.bind(new InetSocketAddress(8001))
                .configureBlocking(false);
        SelectionKey key8001 = channel8081.register(selector, SelectionKey.OP_ACCEPT); //ServerSocketChannel只支持accept事件

        ServerSocketChannel channel8082 = ServerSocketChannel.open();
        channel8082.bind(new InetSocketAddress(8002))
                .configureBlocking(false);
        SelectionKey key8002 = channel8082.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (true) {
            System.out.println("-----检测通道是否就绪-----");
            int count = selector.select();
            System.out.println(String.format("-----%s个通道就绪-----", count));
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                System.out.println(String.format("%s就绪", serverSocketChannel));

                SocketChannel socketChannel = serverSocketChannel.accept();

                int readCount = socketChannel.read(buffer);
                buffer.flip();
                byte[] bytes = new byte[buffer.limit()];
                buffer.get(bytes);

                System.out.println(String.format("读取%s字节", readCount));
                System.out.println(String.format("读取的内容：%s", new String(bytes)));
                keyIterator.remove();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        test();
    }

}
