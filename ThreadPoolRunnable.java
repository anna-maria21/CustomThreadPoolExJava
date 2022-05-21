import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class ThreadPoolRunnable implements Runnable {

    private Thread thread = null;
    private BlockingQueue taskQueue;
    private boolean isStopped = false;
    private Queue<String> times;
    private List<Object> res = new ArrayList<>();

    public ThreadPoolRunnable(BlockingQueue queue, Queue<String> t) {
        taskQueue = queue;
        times = t;
    }

    @Override
    public void run() {
        this.thread = Thread.currentThread();
        while (!isStopped) {
            try {
                Runnable runnable = (Runnable) taskQueue.take();
                times.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                runnable.run();
            } catch (InterruptedException e) {}
        }
    }

    public synchronized void doStop() {
        isStopped = true;
        this.thread.interrupt();
    }

    public synchronized boolean isStopped() {
        return isStopped;
    }

}
