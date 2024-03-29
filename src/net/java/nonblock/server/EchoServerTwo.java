package net.java.nonblock.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-26
 * Time: 上午10:47
 * To change this template use File | Settings | File Templates.
 */
public class EchoServerTwo {
    private Selector selector = null;
    private ServerSocketChannel serverSocketChannel = null;
    private int port = 8000;
    private Charset charset = Charset.forName("GBK");

    public EchoServerTwo() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().setReuseAddress(true);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        System.out.println("Server begin");
    }
    public void service() throws IOException {
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while(selector.select() > 0)
        {
            Set readyKeys = selector.selectedKeys();
            Iterator it = readyKeys.iterator();
            while(it.hasNext())
            {
                SelectionKey key = null;
                                    key = (SelectionKey) it.next();
                    it.remove();
                    if(key.isAcceptable())
                    {
                        ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel)key.channel();
                        SocketChannel socketChannel = serverSocketChannel1.accept();
                        System.out.println("get Client connect:" + socketChannel.socket().getInetAddress() + ":" + socketChannel.socket().getPort());
                        socketChannel.configureBlocking(false);
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, buffer);
                    }
                if(key != null)
                {
                    key.cancel();
                    key.channel().close();
                }
            }
        }
    }

    public void send(SelectionKey key) throws IOException {
        ByteBuffer buffer = (ByteBuffer)key.attachment();
        SocketChannel socketChannel = (SocketChannel) key.channel();
        buffer.flip();
        String data = decode(buffer);
        if(data.indexOf("\r\n") == -1)
            return;
        String outputData = data.substring(0, data.indexOf("\n") + 1);
        System.out.println(outputData);
        ByteBuffer outputBuffer = encode("echo:" + outputData);
        while(outputBuffer.hasRemaining())
        {
            socketChannel.write(outputBuffer);
        }
        ByteBuffer temp = encode(outputData);
        buffer.position(temp.limit());
        buffer.compact();

        if(outputData.equals("bye\r\n"))
        {
            key.cancel();
            socketChannel.close();
            System.out.println("close client connect");
        }

    }

    public void receive(SelectionKey key) throws IOException {
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer readBuff = ByteBuffer.allocate(32);
        socketChannel.read(readBuff);
        readBuff.flip();
        buffer.limit(buffer.capacity());
        buffer.put(readBuff);
    }
    public String decode(ByteBuffer buffer)
    {
        CharBuffer charBuffer = charset.decode(buffer);
        return charBuffer.toString();
    }
    public ByteBuffer encode(String string)
    {
        return charset.encode(string);
    }
    public static void main(String[] args) throws IOException {
        EchoServerTwo serverTwo = new EchoServerTwo();
        serverTwo.service();
    }
}
