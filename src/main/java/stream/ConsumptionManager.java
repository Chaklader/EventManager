package stream;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import models.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;


@Slf4j
@Data
public class ConsumptionManager extends Thread {


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


            /**
             * consumer manager thread will only run till the producer is creating the character items
             */
            while (isProducerAlive.getAsBoolean()) {

                try {
                    log.info("stream.Consumer: " + threadName + " is waiting to take element...");

                    log.info("Number of consumed message : " + numberOfConsumedMessages.intValue());


                    Event consumedEvent = transferQueue.take();

                    char itemChar = consumedEvent.getItem();


                    if (itemChar == '\0') {

                        log.info("Terminating the item character consumption after receiving an especial instruction!!");

                        break;
                    }

                    log.info("stream.Consumer: " + threadName + " received item with id " + consumedEvent.getId() + " and value : " + itemChar);

                    processCreatedEvent(consumedEvent);

                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

            }

            log.info("The production manager thread is not alive anymore and hence, we terminate the consumption procedure");
        }
    }


    /**
     * store the newly created event and increment the event indexes
     */
    private synchronized void processCreatedEvent(Event element) throws InterruptedException {

        numberOfConsumedMessages.incrementAndGet();
        totalEvents.add(element);

        Thread.sleep(5);
    }

}
