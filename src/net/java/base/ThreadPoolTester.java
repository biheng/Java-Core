package net.java.base;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-22
 * Time: 下午2:49
 * To change this template use File | Settings | File Templates.
 */
public class ThreadPoolTester {
    public static void main(String[] args) throws InterruptedException, IllegalAccessException {
        int numTask = 2;
        int poolSize = 3;
        ThreadPool threadPool = new ThreadPool(poolSize);
        for(int i = 0; i < numTask; i++)
        {
            threadPool.execute(createTask(i));

        }
        threadPool.join();


    }

    private static Runnable createTask(final int taskID) {
        return new Runnable() {
            @Override
            public void run() {
                System.out.println("Task" + taskID + ":start");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        };  //To change body of created methods use File | Settings | File Templates.
    }
}
