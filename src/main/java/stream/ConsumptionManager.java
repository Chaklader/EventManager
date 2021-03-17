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


    public List<Event> getAllConsumedEvents() {

        return totalEvents;
    }


    @Override
    public void run() {

        synchronized (this) {


            /**
             * consumer manager thread will only run till the producer is creating the character items
             */
            while (isProducerAlive.getAsBoolean()) {

                try {
                    LOG.info("stream.Consumer: " + threadName + " is waiting to take element...");

                    LOG.info("Number of consumed message : " + numberOfConsumedMessages.intValue());


                    Event consumedEvent = transferQueue.take();

                    char itemCharacter = consumedEvent.getItem();


                    if (itemCharacter == '\0') {

                        LOG.info("Terminating the item character consumption after receiving an especial instruction!!");

                        break;
                    }

                    LOG.info("stream.Consumer: " + threadName + " received item with id " + consumedEvent.getId() + " and value : " + itemCharacter);

                    processEvent(consumedEvent);

                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

            }

            LOG.info("The production manager thread is not alive anymore and hence, we terminate the consumption procedure");
        }
    }

    private synchronized void processEvent(Event element) throws InterruptedException {

        numberOfConsumedMessages.incrementAndGet();
        totalEvents.add(element);

        Thread.sleep(5);
    }

}
