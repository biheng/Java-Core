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
 * Time: 上午11:14
 * To change this template use File | Settings | File Templates.
 */
public class EchoServerThree {
    private Selector selector = null;
    private ServerSocketChannel serverSocketChannel = null;
    private int port = 8000;
    private Charset charset = Charset.forName("GBK");
    private Object gate = new Object();
    public EchoServerThree() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().setReuseAddress(true);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        System.out.println("Server begin");
    }
    public void accept() throws IOException {
        while(true)
        {
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println("get Client connect :" +  socketChannel.socket().getInetAddress() + socketChannel.socket().getPort());
            socketChannel.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            synchronized (gate)
            {
                selector.wakeup();
                socketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE, buffer);

            }
        }
    }
    public void service() throws IOException {

        while(true)
        {
            synchronized (gate){}
            int n = selector.select();
            if(n == 0) continue;

            Set readyKeys = selector.selectedKeys();
            Iterator it = readyKeys.iterator();
            while(it.hasNext())
            {
                SelectionKey key = null;
                key = (SelectionKey) it.next();
                it.remove();
                if(key.isReadable())
                {
                    receive(key);
                }
                if(key.isWritable())
                {
                    send(key);
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
        final EchoServerThree serverThree= new EchoServerThree();
        Thread accept = new Thread()
        {
            public void run()
            {
                try {
                    serverThree.accept();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        };
        accept.start();
        serverThree.service();
    }
}
