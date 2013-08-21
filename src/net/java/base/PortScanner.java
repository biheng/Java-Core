package net.java.base;

import java.io.IOException;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-21
 * Time: 下午3:03
 * To change this template use File | Settings | File Templates.
 */
public class PortScanner {
    public void scan(String host)
    {
        Socket socket = null;
        for(int port = 1; port < 1024; port++)
        {
            try {
                socket = new Socket(host, port);
                System.out.println("There is a server on port " + port);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.out.println("Can't connect to port" + port);
            }finally {
                if(socket != null)
                {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }


        }
    }
}
