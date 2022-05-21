import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CustomThreadPool {

    private BlockingQueue<Runnable> taskQueue;
    private final List<ThreadPoolRunnable> runnables = new ArrayList<>();
    private boolean isStopped = false;
    private Queue<String> times;
    private List<Object> res;

    public CustomThreadPool(int numOfThreads, int maxThreads) {

        taskQueue = new ArrayBlockingQueue<>(maxThreads);
        times = new LinkedList<>();
        res = new ArrayList<>();

        for (int i = 0; i < numOfThreads; i++) {
            ThreadPoolRunnable threadPoolRunnable = new ThreadPoolRunnable(taskQueue, times);
            runnables.add(threadPoolRunnable);
        }
        for (ThreadPoolRunnable runnable : runnables) {
            new Thread(runnable).start();
        }
    }

    public synchronized List<Object> map(String func, List objectList) {

        for (Object obj : objectList) {

            if (this.isStopped) {
                throw new IllegalStateException("ThreadPool is stopped!");
            }

            Class clazz = ThreadPoolMain.class;
            Method[] methodsInMain = clazz.getMethods();
            for (Method m : methodsInMain) {
                if (Objects.equals(m.getName(), func)) {
                    this.taskQueue.offer(() -> {
                        Object temp = null;
                        try {
                            temp = m.invoke(null, obj);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        System.out.println(times.poll() + " - " + temp);
                        res.add(temp);
                    });
                }
            }
        }
        return this.res;
    }

    public synchronized void shutdown() {
        while(this.taskQueue.size() > 0) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}
        }

        this.isStopped = true;
        for (ThreadPoolRunnable runnable : runnables) {
            runnable.doStop();
        }
    }

}
