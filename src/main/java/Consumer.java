import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.logging.Logger;


public class Consumer extends Thread {


    private static final Logger LOG = Logger.getLogger(Consumer.class.getName());


    private final TransferQueue<Event> transferQueue;

    private final String threadName;

    private final AtomicInteger numberOfConsumedMessages = new AtomicInteger();

    private final List<Event> totalEvents = new ArrayList<>();

    private volatile BooleanSupplier isProducerAlive;


    Consumer(BooleanSupplier isProducerAlive, TransferQueue<Event> transferQueue, String threadName) {

        this.transferQueue = transferQueue;
        this.threadName = threadName;

        this.isProducerAlive = isProducerAlive;
    }

    @Override
    public void run() {

        synchronized (this) {

            while (isProducerAlive.getAsBoolean()) {

                try {

                    LOG.info("Consumer: " + threadName + " is waiting to take element...");

                    LOG.info("Number of consumed message : " + numberOfConsumedMessages.intValue());

                    Event event = transferQueue.take();

                    char item = event.getItem();

                    if (item == '\0') {

                        isProducerAlive = () -> false;

                        LOG.info("The consumer thread is terminating the consumption procedure");
                        break;
                    }

                    processEvent(event);

                    LOG.info("Consumer: " + threadName + " received item with value : " + item);

                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

            }
        }
    }

    private void processEvent(Event element) throws InterruptedException {

        numberOfConsumedMessages.incrementAndGet();
        totalEvents.add(element);

        Thread.sleep(5);
    }

    public List<Event> getAllConsumedEvents() {

        return totalEvents;
    }

}
