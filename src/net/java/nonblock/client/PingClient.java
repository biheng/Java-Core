package net.java.nonblock.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-26
 * Time: 下午2:07
 * To change this template use File | Settings | File Templates.
 */
public class PingClient {
    private Selector selector;
    private LinkedList targets = new LinkedList();
    private LinkedList finishedTargets = new LinkedList();

    public PingClient() throws IOException {
        selector = Selector.open();
        Connector connector = new Connector();
        Printer printer = new Printer();
        connector.start();
        printer.start();
        receiveTarget();


    }
    public static void main(String[] args) throws IOException {
        new PingClient();
    }
    public void addTarget(Target target)
    {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(target.address);
            target.channel = socketChannel;
            target.connectStart = System.currentTimeMillis();
            synchronized (targets)
            {
                targets.add(target);
            }
            selector.wakeup();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            if(socketChannel != null)
            {
                try{
                    socketChannel.close();
                }catch (IOException ee)
                {

                }
            }
            target.failure = e;
            addFinishedTarget(target);
        }
    }

    private void addFinishedTarget(Target target) {
        synchronized (finishedTargets)
        {
            finishedTargets.notify();
            finishedTargets.add(target);
        }
    }

    public void printFinishedTargets()
    {
        try
        {
            while(true)
            {
                Target target = null;
                synchronized (finishedTargets)
                {
                    while(finishedTargets.size() == 0)
                    {
                        finishedTargets.wait();

                    }
                    target = (Target)finishedTargets.removeFirst();
                }
                target.show();
            }
        }catch (InterruptedException e)
        {
            return;
        }

    }

    public void registerTargets()
    {
        synchronized (targets)
        {
            while(targets.size() > 0)
            {
                Target target
                         = (Target)targets.removeFirst();
                try {
                    target.channel.register(selector, SelectionKey.OP_CONNECT, target);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    try {
                        target.channel.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    target.failure = e;
                    addFinishedTarget(target);

                }
            }
        }
    }
    public void processSelectedKeys() throws IOException {
        for(Iterator it = selector.selectedKeys().iterator(); ;it.hasNext())
        {
            SelectionKey selectionKey = (SelectionKey)it.next();
            it.remove();
            Target target = (Target) selectionKey.attachment();
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            try{
                if(socketChannel.finishConnect())
                {
                    selectionKey.cancel();
                    target.connectFinish = System.currentTimeMillis();
                    socketChannel.close();
                    addFinishedTarget(target);
                }
            }catch (IOException x)
            {
                socketChannel.close();
                target.failure = x;
                addFinishedTarget(target);
            }
        }

    }

    public void receiveTarget()
    {
        try {
            BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
            String msg = null;
            while((msg = localReader.readLine()) != null)
            {
                if(!msg.equals("bye"))
                {
                    Target target
                             = new Target(msg);
                    addTarget(target);
                }else
                {
                    shutdown = true;
                    selector.wakeup();
                    break;
                }
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    boolean shutdown = false;
    public  class Printer extends  Thread
    {
        public Printer() {
            setDaemon(true);

        }
        public void run()
        {
            printFinishedTargets();
        }
    }
    public class Connector extends Thread
    {
        public void run()
        {
            while(!shutdown)
            {
                try{
                    registerTargets();
                    if(selector.select() > 0)
                    {
                        processSelectedKeys();
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }
}
class  Target
{
    InetSocketAddress address;
    SocketChannel channel;
    Exception failure;
    long connectStart;
    long connectFinish = 0;
    boolean shown = false;

    Target(String host)
    {
        try
        {
            address = new InetSocketAddress(InetAddress.getByName(host), 80);

        }catch ( IOException x)
        {
            failure = x;
        }
    }
    void show()
    {
        String result;
        if(connectFinish != 0)
        {
            result = Long.toString(connectFinish - connectStart) + "ms";
        }else if(failure != null)
        {
            result = failure.toString();

        }else
        {
            result = "Timed out";
        }
        System.out.println(address + ":" + result);
        shown = true;
    }
}