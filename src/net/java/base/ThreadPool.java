package net.java.base;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-22
 * Time: 下午2:27
 * To change this template use File | Settings | File Templates.
 */
public class ThreadPool extends ThreadGroup{
    private boolean isClosed = false;     //线程池是否关闭
    private LinkedList<Runnable> workQueue; //工作队列
    private static int threadPoolID;         //线程池id
    private int threadId;                   //线程id

    public ThreadPool(int poolSize)
    {
        super("ThreadPool-" + (threadPoolID++));
        setDaemon(true);
        workQueue = new LinkedList<Runnable>();
        for(int i = 0; i < poolSize; i++)
        {
            new WorkThread().start();
        }
    }

    public ThreadPool(String name) {
        super(name);
    }

    public synchronized  void execute(Runnable task) throws IllegalAccessException {
        if(isClosed)
        {
            throw new IllegalAccessException();
        }
        if(task != null)
        {
            workQueue.add(task);
            notify();//唤醒getTask()方法中等待任务的工作线程
        }
    }

    protected synchronized Runnable getTask()throws  InterruptedException
    {
        while (workQueue.size() == 0)
        {
            if(isClosed)
            {
                return null;

            }
            wait();
        }
        return  workQueue.removeFirst();
    }

    public synchronized  void close()
    {
        if(!isClosed)
        {
            isClosed = true;
            workQueue.clear();
            interrupt(); //中断所有的工作线程
        }
    }

    public void join() throws InterruptedException {
        synchronized (this)
        {
            isClosed = true;
            notifyAll();
        }
        Thread[] threads = new Thread[activeCount()];
        int count = enumerate(threads);
        for(int i = 0; i < count; i++)
        {
            threads[i].join();
        }
    }

    private class  WorkThread extends Thread{
        public WorkThread()
        {
            super(ThreadPool.this, "WorkThread-" + (threadPoolID++));
        }
        public void run()
        {
            while (!isInterrupted())
            {
                Runnable task = null;
                try {
                    task = getTask();
                }catch (InterruptedException ex)
                {

                }
                if(task == null)
                {
                    return;
                }
                try {
                    task.run();
                }
                catch (Throwable t)
                {

                }

            }
        }
    }
}
