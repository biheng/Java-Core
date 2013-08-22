package net.java.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-21
 * Time: 下午1:37
 * To change this template use File | Settings | File Templates.
 */
public class EchoServer {
    private int port = 8000;
    private ServerSocket serverSocket;
    //private  ThreadPool threadPool;
    private ExecutorService threadPool;
    private  final  int POOL_SIZE = 4;

    private int portForShutDown = 8001;
    private ServerSocket serverSocketForShutdown;
    private boolean isShutdown = false;
    private  Thread shudownThread = new Thread()
    {
        public void start()
        {
            this.setDaemon(true);
            super.start();
        }

        @Override
        public void run() {
            while(!isShutdown)
            {
                Socket socketForShutdown = null;
                try {
                    socketForShutdown = serverSocketForShutdown.accept();
                    BufferedReader bufferedReader
                             = new BufferedReader(new InputStreamReader(socketForShutdown.getInputStream()));
                    String command = bufferedReader.readLine();
                    if(command.equals("shutdown"))
                    {
                        long beginTime = System.currentTimeMillis();
                        socketForShutdown.getOutputStream().write("server shutdowning\r\n".getBytes());
                        isShutdown = true;
                        threadPool.shutdown();
                        while (!threadPool.isTerminated())
                        {
                            threadPool.awaitTermination(10, TimeUnit.SECONDS);
                        }
                        serverSocket.close();
                        long endTime = System.currentTimeMillis();
                        socketForShutdown.getOutputStream().write(((endTime - beginTime) + "millis").getBytes());
                        socketForShutdown.close();
                    }else
                    {
                        socketForShutdown.getOutputStream().write("wrong cmd\r\n".getBytes());
                        socketForShutdown.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    };

    public EchoServer() throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(60000);
        serverSocketForShutdown = new ServerSocket(portForShutDown);
        //threadPool =new ThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);

        threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
        shudownThread.start();
        System.out.print("server begin");
    }

    public String echo(String msg) {
        return "echo" + msg;
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        return new PrintWriter(outputStream, true); //auto flush true
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    public void service() throws IOException, InterruptedException, IllegalAccessException {
        while (!isShutdown) {

            Socket socket;
            socket = serverSocket.accept();
            socket.setSoTimeout(60000);
            threadPool.execute(new Handler(socket));
            //Thread workThread = new Thread(new Handler(socket));
            //workThread.start();
        }
    }

    public static void main(String[] args) throws IllegalAccessException {
        try {
            new EchoServer().service();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private class Handler implements Runnable {
        Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.print("\nNew connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
            try {
                BufferedReader bufferedReader = getReader(socket);
                PrintWriter printWriter = getWriter(socket);
                String msg = null;
                while ((msg = bufferedReader.readLine()) != null) {
                    System.out.println(msg);
                    printWriter.println(echo(msg));
                    if (msg.equals(("bye"))) {
                        break;
                    }
                }
                System.out.print("ok");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
