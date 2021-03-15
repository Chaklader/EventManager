import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Created by Chaklader on Mar, 2021
 */
public class App {


    private static final Logger LOG = Logger.getLogger(App.class.getName());


    public static void main(String[] args) throws InterruptedException {


        List<Event> events = new ArrayList<>();

        Object o = new Object();

        TransferQueue<Event> transferQueue = new LinkedTransferQueue<>();
        ExecutorService exService = Executors.newFixedThreadPool(2);

        Producer producer = new Producer( transferQueue, "1", 5);
        Consumer consumer = new Consumer(transferQueue, "1", 3);

        exService.execute(producer);
        exService.execute(consumer);

        boolean isShutDown = exService.awaitTermination(5000, TimeUnit.MILLISECONDS);

        if (!isShutDown) {

            exService.shutdown();
        }


        LOG.info("Transfer completed");
        System.exit(0);
    }
}
