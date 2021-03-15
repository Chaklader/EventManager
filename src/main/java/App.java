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


        TransferQueue<Event> transferQueue = new LinkedTransferQueue<>();
        ExecutorService exService = Executors.newFixedThreadPool(2);

        Producer producer = new Producer( transferQueue, "1", 2);
        producer.start();

        Consumer consumer = new Consumer(producer::isAlive, transferQueue, "1", 2);
        consumer.start();

        exService.execute(producer);
        exService.execute(consumer);


        boolean isShutDown = exService.awaitTermination(5000, TimeUnit.MILLISECONDS);

        if (!isShutDown) {

            exService.shutdown();
        }




        List<Event> sdds = consumer.getEvents();

        EventProcessor processor = new EventProcessor(sdds, 5);
        String v = processor.createString();
        String sample = processor.createSample(v);

        System.out.println(sample);


        LOG.info("Transfer completed");
    }
}
