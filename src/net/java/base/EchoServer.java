package net.java.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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

    public EchoServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.print("server begin");
    }

    public String echo(String msg)
    {
        return "echo" +  msg;
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        return  new PrintWriter(outputStream, true); //auto flush true
    }
    private BufferedReader getReader(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    public void service() throws IOException, InterruptedException {
        while(true)
        {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                System.out.print("\nNew connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
                BufferedReader bufferedReader = getReader(socket);
                PrintWriter printWriter = getWriter(socket);
                String msg = null;
                while ( (msg = bufferedReader.readLine()) != null)
                {
                    System.out.println(msg);
                    printWriter.println(echo(msg));
                    if(msg.equals(("bye")))
                    {
                        break;
                    }
                }
                System.out.print("ok");
            }catch (IOException e)
            {
                e.printStackTrace();
            }finally {
                try{
                    if(socket != null)
                    {
                        socket.close();
                    }
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String[] args)
    {
        try {
            new EchoServer().service();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
