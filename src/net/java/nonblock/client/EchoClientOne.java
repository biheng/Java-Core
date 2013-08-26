package net.java.nonblock.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-26
 * Time: 上午11:34
 * To change this template use File | Settings | File Templates.
 */
public class EchoClientOne {
    private SocketChannel socketChannel = null;
    public EchoClientOne()throws IOException
    {
        socketChannel = SocketChannel.open();
        InetAddress ia = InetAddress.getLocalHost();
        InetSocketAddress isa = new InetSocketAddress(ia, 8000);
        socketChannel.connect(isa);
        System.out.println("get Server connect successful");
    }
    public static void main(String args[]) throws IOException
    {
        new EchoClientOne().talk();
    }
    public void talk()throws IOException
    {
        BufferedReader bufferedReader = getReader(socketChannel.socket());
        PrintWriter pw = getWriter(socketChannel.socket());
        BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
        String msg = null;
        while((msg = localReader.readLine()) != null)
        {
            pw.println(msg);
            System.out.println(bufferedReader.readLine());
            if(msg.equals("bye"))
            {

                break;
            }
        }
        if(socketChannel != null)
        {
            socketChannel.close();
        }

    }

    private PrintWriter getWriter(Socket socket) throws IOException {

        return new PrintWriter(socket.getOutputStream(), true);
    }

    private BufferedReader getReader(Socket socket) throws IOException {

        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
}
