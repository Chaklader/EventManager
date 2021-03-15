import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Consumer implements Runnable {

    private static final Logger LOG = Logger.getLogger(Consumer.class.getName());

    private final TransferQueue<Event> transferQueue;

    private final String name;

    final int numberOfMessagesToConsume;

    final AtomicInteger numberOfConsumedMessages = new AtomicInteger();

    final List<Event> events = new ArrayList<>();



    public List<Event> getEvents() {
        return events;
    }

    Consumer(TransferQueue<Event> transferQueue, String name, int numberOfMessagesToConsume) {

        this.transferQueue = transferQueue;
        this.name = name;
        this.numberOfMessagesToConsume = numberOfMessagesToConsume;

    }

    Event element;

    @Override
    public void run() {


        synchronized (this) {

            while (true) {

                try {
                    LOG.info("Consumer: " + name + " is waiting to take element...");

                    element = transferQueue.take();

                    Character c = element.getC();

                    if (c.equals('\0')) {

                        LOG.info("Received the ending hook and terminating the consumption procedure");
                        break;
                    }

                    longProcessing(element);

                    System.out.println("Consumer: " + name + " received element with messgae : " + c);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void longProcessing(Event element) throws InterruptedException {

        numberOfConsumedMessages.incrementAndGet();
        events.add(element);

        Thread.sleep(5);
    }

}
