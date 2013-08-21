package net.java.base;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-21
 * Time: 下午3:24
 * To change this template use File | Settings | File Templates.
 */
public class ConnectTest {
    public void connect(String host, int port)
    {
        SocketAddress remoteSocketAddress = new InetSocketAddress(host, port);
        Socket socket = null;
        String result = null;
        try {
            long begin = System.currentTimeMillis();
            socket = new Socket();
            socket.connect(remoteSocketAddress, 10000);
            long end = System.currentTimeMillis();
            result = (end - begin) + "ms";
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
