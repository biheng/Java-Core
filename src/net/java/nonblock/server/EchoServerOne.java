package net.java.nonblock.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-26
 * Time: 上午10:12
 * To change this template use File | Settings | File Templates.
 */
public class EchoServerOne{
    private int port = 8000;
    private ServerSocketChannel serverSocketChannel = null;
    private ExecutorService executorService;
    private static final int POOL_MULTIPLE = 4;

    public EchoServerOne() throws IOException {
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_MULTIPLE);
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().setReuseAddress(true);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        System.out.println("Server begin");
    }

    public void service() {
        while (true) {
            SocketChannel socketChannel = null;
            try {
                socketChannel = serverSocketChannel.accept();
                executorService.execute(new Handler(socketChannel));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public static void main(String args[]) throws IOException {
        new EchoServerOne().service();
    }

    private class Handler implements Runnable {
        private SocketChannel socketChannel;

        public Handler(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
            handle(socketChannel);
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void handle(SocketChannel socketChannel) {
            try {
                Socket socket = socketChannel.socket();
                System.out.println("Get a client connect:" + socket.getInetAddress() + ":" + socket.getPort());
                BufferedReader bufferedReader = getReader(socket);
                PrintWriter printWriter = getWriter(socket);
                String msg = null;
                while ((msg = bufferedReader.readLine()) != null) {
                    System.out.println(msg);
                    printWriter.println(echo(msg));
                    if (msg.equals("bye")) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (socketChannel != null) {
                    try {
                        socketChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

            }

        }
    }

    private String echo(String msg) {
        return "echo:" + msg;

    }

    private PrintWriter getWriter(Socket socket) {
        try {
            new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    private BufferedReader getReader(Socket socket) {
        try {
            return new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
