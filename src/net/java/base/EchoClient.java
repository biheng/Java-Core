package net.java.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-21
 * Time: 下午1:57
 * To change this template use File | Settings | File Templates.
 */
public class EchoClient {
    private final String host = "localhost";
    private final int port = 8000;
    private Socket socket;

    public EchoClient() throws IOException {
        socket = new Socket(host, port);
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);//auto flush true
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void talk() {
        try {
            BufferedReader bufferedReader = getReader(socket);
            BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter printWriter = getWriter(socket);
            String msg = null;
            while ((msg = localReader.readLine()) != null) {
                System.out.print(msg);
                printWriter.println(msg);
                System.out.println(bufferedReader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            new EchoClient().talk();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
