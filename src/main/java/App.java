import java.util.List;
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

        Producer producer = new Producer(transferQueue, "producer thread");
        producer.start();

        Consumer consumer = new Consumer(producer::isAlive, transferQueue, "consumer thread");
        consumer.start();

        producer.join();
        consumer.join();

        List<Event> allConsumedEvents = consumer.getAllConsumedEvents();

        EventProcessor processor = new EventProcessor(allConsumedEvents, 5);

        String consumedStr = processor.getStringUsingConsumedCharacters();
        String randomSampleRes = processor.createRandomSample(consumedStr);


        LOG.info("Generated random sample from the consumed string: " + randomSampleRes);

        LOG.info("Transfer completed");
    }
}
