package stream;


import models.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.logging.Logger;

public class ConsumptionManager extends Thread {


    private static final Logger LOG = Logger.getLogger(ConsumptionManager.class.getName());


    private final TransferQueue<Event> transferQueue;

    private final String threadName;

    private final AtomicInteger numberOfConsumedMessages = new AtomicInteger();

    private final List<Event> totalEvents = new ArrayList<>();

    private final BooleanSupplier isProducerAlive;


    public ConsumptionManager(BooleanSupplier isProducerAlive, TransferQueue<Event> transferQueue, String threadName) {

        this.transferQueue = transferQueue;
        this.threadName = threadName;

        this.isProducerAlive = isProducerAlive;
    }

    @Override
    public void run() {

        synchronized (this) {

            while (isProducerAlive.getAsBoolean()) {

                try {

                    LOG.info("Condition of the producer thread is alive? " + isProducerAlive.getAsBoolean());

                    LOG.info("stream.Consumer: " + threadName + " is waiting to take element...");

                    LOG.info("Number of consumed message : " + numberOfConsumedMessages.intValue());



                    Event event = transferQueue.take();

                    char itemCharacter = event.getItem();

                    LOG.info("stream.Consumer: " + threadName + " received item with id " + event.getId() + " and value : " + itemCharacter);

                    processEvent(event);

                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

            }
        }
    }

    private synchronized void processEvent(Event element) throws InterruptedException {

        numberOfConsumedMessages.incrementAndGet();
        totalEvents.add(element);

        Thread.sleep(5);
    }

    public List<Event> getAllConsumedEvents() {

        return totalEvents;
    }

}
