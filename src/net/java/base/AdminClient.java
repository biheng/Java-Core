package net.java.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-22
 * Time: 下午3:46
 * To change this template use File | Settings | File Templates.
 */
public class AdminClient {

    public static void main(String[] args) throws IOException {
        Socket socket = null;
        socket = new Socket("localhost", 8001);
        OutputStream socketOut = socket.getOutputStream();
        socketOut.write("shutdown\r\n".getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String msg = null;
        while ((msg = bufferedReader.readLine()) != null)
        {
            System.out.println(msg);
        }
        if(socket != null)
        {
            socket.close();
        }
    }

}
