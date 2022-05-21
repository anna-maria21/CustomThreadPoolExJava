import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ThreadPoolMain {

    public static void main(String[] args) throws Exception {

        System.out.println("Start time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
        CustomThreadPool threadPool = new CustomThreadPool(2, 10);

        List<Object> res = threadPool.map("longRunningTask", List.of(1, 2, 3, 4));

        threadPool.shutdown();

        System.out.println("\nEnd time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    public static int longRunningTask(int x) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}

        return x * 2;
    }
}
