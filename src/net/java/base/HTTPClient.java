package net.java.base;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-21
 * Time: 下午3:40
 * To change this template use File | Settings | File Templates.
 */
public class HTTPClient {
    private final java.lang.String host = "s1.bdstatic.com";
    private final int port = 80;
    private Socket socket;

    public HTTPClient() throws IOException {
        socket = new Socket(host, port);
    }

    public void communicate()throws Exception{
        StringBuffer sb = new StringBuffer("GET"  + "s1.bdstatic.com/r/www/cache/static/global/js/home_ef347748.js" + " HTTP/1.1\r\n");
        sb.append("Host:s1.bdstatic.com\r\n");
        sb.append("Accept: */*\r\n");
        sb.append("Accept-Language:zh-cn\r\n");
        sb.append("Accept-Encoding:gzip, deflate\r\n");
        sb.append("User-Agent:Mozilla/4.0(X11; Ubuntu; Linux i686; rv:21.0)\r\n");
        sb.append("Connection:Keep-Alive\r\n\r\n");

        OutputStream socketOut = socket.getOutputStream();
        socketOut.write(sb.toString().getBytes());
        socket.shutdownOutput();

        InputStream inputStream = socket.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len = -1;
        while((len = inputStream.read(buff)) != -1)
        {
            byteArrayOutputStream.write(buff, 0, len);
        }
        System.out.println(new String(byteArrayOutputStream.toByteArray()));
        socket.close();
    }

    public void createSocket() throws IOException {
        socket = new Socket(host, port);
    }
}
