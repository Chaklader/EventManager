import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.logging.Logger;

public class Consumer extends Thread {


    private static final Logger LOG = Logger.getLogger(Consumer.class.getName());

    private final TransferQueue<Event> transferQueue;

    private final String name;

    final int numberOfMessagesToConsume;

    final AtomicInteger numberOfConsumedMessages = new AtomicInteger();

    final List<Event> events = new ArrayList<>();

    volatile BooleanSupplier booleanSupplier;


    public List<Event> getEvents() {
        return events;
    }

    Consumer(BooleanSupplier booleanSupplier, TransferQueue<Event> transferQueue, String name, int numberOfMessagesToConsume) {

        this.transferQueue = transferQueue;
        this.name = name;
        this.numberOfMessagesToConsume = numberOfMessagesToConsume;

        this.booleanSupplier = booleanSupplier;

    }

    Event element;

    @Override
    public void run() {


        synchronized (this) {

            while (booleanSupplier.getAsBoolean()) {

                try {

                    System.out.println("Producer alive " + booleanSupplier.getAsBoolean());
                    LOG.info("Consumer: " + name + " is waiting to take element...");

                    element = transferQueue.take();

                    char c = element.getC();

                    System.out.println("Number of consimed message: " + numberOfConsumedMessages.intValue());

                    if (c == '\0') {


                        booleanSupplier = ()-> false;
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
