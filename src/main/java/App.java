import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;

/**
 * Created by Chaklader on Mar, 2021
 */
public class App {


    private static final Logger LOG = Logger.getLogger(App.class.getName());


    public static void main(String[] args) throws InterruptedException {


        TransferQueue<Event> transferQueue = new LinkedTransferQueue<>();
        ExecutorService exService = Executors.newFixedThreadPool(2);

        Producer producer = new Producer(transferQueue, "producer thread");
        producer.start();

        Consumer consumer = new Consumer(producer::isAlive, transferQueue, "consumer thread");
        consumer.start();

//        exService.execute(producer);
//        exService.execute(consumer);
//
//
//        boolean isShutDown = exService.awaitTermination(5000, TimeUnit.MILLISECONDS);
//
//        if (!isShutDown) {
//
//            exService.shutdown();
//
//            producer.interrupt();
//        }


        if (producer.isInterrupted()) {

            producer.interrupt();

            LOG.info("Producer is interrupted and terminating it now ...");
        }

//        if(producer.isAlive()){
//
//            producer.interrupt();
//        }
//        consumer.interrupt();

//        producer.join();
//        consumer.join();


        List<Event> allConsumedEvents = consumer.getAllConsumedEvents();

        EventProcessor processor = new EventProcessor(allConsumedEvents, 5);

        String v = processor.getStringUsingConsumedCharacters();
        String sample = processor.createSample(v);

        System.out.println(sample);


        LOG.info("Transfer completed");
    }
}
