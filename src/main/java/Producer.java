import jdk.jshell.execution.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;


public class Producer implements Runnable {


    private static final Logger LOG = Logger.getLogger(Producer.class.getName());

    private final TransferQueue<Event> transferQueue;
    private final String name;
    final Integer numberOfMessagesToProduce;
    final AtomicInteger numberOfProducedMessages = new AtomicInteger();



    final List<Event> events = new ArrayList<>();

    Producer(TransferQueue<Event> transferQueue, String name, Integer numberOfMessagesToProduce) {

        this.transferQueue = transferQueue;
        this.name = name;
        this.numberOfMessagesToProduce = numberOfMessagesToProduce;
    }

    @Override
    public void run() {


        synchronized (this) {

            Stream<Character> generate = Stream.generate(this::generateRandomCharacter).limit(15);

            Stream<Character> concat = Stream.concat(generate, Stream.of('\0'));

            concat.forEach(character -> {

                LOG.info("Producer: " + name + " is waiting to transfer...");

                try {

                    Event myEvent = new Event(character);

                    events.add(myEvent);

                    boolean added = transferQueue.tryTransfer(myEvent, 4000, TimeUnit.MILLISECONDS);

                    if (added) {
                        numberOfProducedMessages.incrementAndGet();
                        LOG.info("/Producer: " + name + " transferred element: A");
                    } else {
                        LOG.info("can not add an element due to the timeout");
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
        }


    }

    private Character generateRandomCharacter() {

        Random rnd = new Random();
        char c = (char) ('a' + rnd.nextInt(26));

        return c;
    }


}
