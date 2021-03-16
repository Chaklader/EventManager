import org.apache.commons.lang3.mutable.MutableBoolean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;


public class Producer extends Thread {


    private volatile boolean isRunning = false;

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

    List<Character> chs = new ArrayList<>();


    @Override
    public void run() {


        synchronized (this) {


            MutableBoolean ongoing = new MutableBoolean(true);

            Stream<Character> generate = Stream.generate(this::generateRandomCharacter).takeWhile(bol -> ongoing.getValue());

            Stream<Character> concat = Stream.concat(generate, Stream.of('\0'));

            concat.takeWhile(s -> ongoing.booleanValue()).forEach(character -> {

//                if (isRunning) {
//                    System.out.println();
//                    return;
//                }

                chs.add(character);

                if(chs.size()%1000==0){

                    System.out.println("Hello");
                }

                if (chs.size() == 1000 && check(chs)) {

                    System.out.println("cant add more");
                    currentThread().interrupt();
                    ongoing.setFalse();
                    return;
                }

                LOG.info("Producer: " + name + " is waiting to transfer...");

                try {

                    Event myEvent = createEvent(character);

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


    public AtomicInteger getNumberOfProducedMessages() {
        return numberOfProducedMessages;
    }

    private Event createEvent(char a){

        Event event = new Event();

        event.setC(a);
        event.setDate(new Date());
        event.setId(getNumberOfProducedMessages().get());

        return event;

    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    private char generateRandomCharacter() {

        Random rnd = new Random();

        return (char) ('A' + rnd.nextInt(26));
    }


    private boolean check(List<Character> list) {

        int count = 0;
        boolean res = false;


        for (char character : list) {

            if (
                character == 'A' || character == 'B' || character == 'C' ||
                    character == 'P' || character == 'Q' || character == 'R' ||
                    character == 'X' || character == 'Y' || character == 'Z'
            )

                count++;

            if (count >= 100) {

                res = true;
                break;
            }
        }

        list.clear();
        return res;
    }


}
