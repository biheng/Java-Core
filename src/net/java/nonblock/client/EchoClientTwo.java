package net.java.nonblock.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-26
 * Time: 下午1:35
 * To change this template use File | Settings | File Templates.
 */
public class EchoClientTwo {
    private SocketChannel socketChannel = null;
    private  ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
    private Charset charset = Charset.forName("GBK");
    private Selector selector;
    public EchoClientTwo() throws IOException {
        socketChannel = SocketChannel.open();
        InetAddress ia = InetAddress.getLocalHost();
        InetSocketAddress isa = new InetSocketAddress(ia, 8000);
        socketChannel.connect(isa);
        socketChannel.configureBlocking(false);
        System.out.println("get Server connect ...");
        selector = Selector.open();
    }

    public static void main(String[] args) throws IOException {
        final EchoClientTwo clientTwo = new EchoClientTwo();
        Thread receiver = new Thread()
        {
            public void run()
            {
                clientTwo.receiveFormUser();
            }
        };
        receiver.start();
        clientTwo.talk();
    }

    private void receiveFormUser() {
        BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader msg = null;
        while((msg=localReader) != null)
        {
            synchronized (sendBuffer)
            {
                sendBuffer.put(encode(msg+"\r\n"));
            }
            if(msg.equals("bye"))
            {
                break;
            }
        }
    }

    private void talk() throws IOException {
        socketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
        while(selector.select()>0)
        {
            Set readyKeys = selector.selectedKeys();
            Iterator it = readyKeys.iterator();
            while(it.hasNext())
            {
                SelectionKey key = null;
                key = (SelectionKey)it.next();
                it.remove();
                if(key.isReadable())
                {
                    receive(key);
                }
                if(key.isWritable())
                {
                    send(key);
                }
            }
        }
    }

    private void receive(SelectionKey key) throws IOException {
        SocketChannel socketChannel1 = (SocketChannel)key.channel();
        socketChannel1.read(receiveBuffer);
        receiveBuffer.flip();
        String receiveData = decode(receiveBuffer);
        if(receiveData.indexOf("\n") == -1)
        {
            return;
        }
        String outputdata = receiveData.substring(0, receiveData.indexOf("\n") + 1);
        System.out.println(outputdata);
        if(outputdata.equals("echo:byt\r\n"))
        {
            key.cancel();
            try {
                socketChannel1.close();
                System.out.println("close connect");
                selector.close();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

        ByteBuffer temp = encode(outputdata);
        receiveBuffer.position(temp.limit());
        receiveBuffer.compact();
    }

    private String decode(ByteBuffer receiveBuffer) {
        return charset.decode(receiveBuffer).toString();
    }

    private ByteBuffer encode(String outputdata) {
        return charset.encode(outputdata);
    }

    private void send(SelectionKey key) {
        SocketChannel socketChannel1 = (SocketChannel) key.channel();
        synchronized (sendBuffer)
        {
            sendBuffer.flip();
            try {
                socketChannel1.write(sendBuffer);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            sendBuffer.compact();
        }
    }

}
