package net.java.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-21
 * Time: 上午9:45
 * To change this template use File | Settings | File Templates.
 */
public class EchoPlayer {
    public String echo(String msg)
    {
        return "echo:" + msg;
    }
    public void talk()throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String msg = null;
        while((msg = bufferedReader.readLine() )!= null)
        {
            System.out.println(echo(msg));
            if(msg.equals("bye"))
                break;
        }
    }

    public static void main(String[] args) throws  IOException
    {
        new EchoPlayer().talk();
    }
}
