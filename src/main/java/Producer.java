import org.apache.commons.lang3.mutable.MutableBoolean;

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


    private static final Logger LOG = Logger.getLogger(Consumer.class.getName());


    private final TransferQueue<Event> transferQueue;

    private final String threadName;

    private final AtomicInteger numberOfProducedMessages = new AtomicInteger();

    private final List<Character> characters = new ArrayList<>();

    private final List<Event> events = new ArrayList<>();


    Producer(TransferQueue<Event> transferQueue, String threadName) {

        this.transferQueue = transferQueue;
        this.threadName = threadName;
    }


    @Override
    public void run() {


        synchronized (this) {

            MutableBoolean isKeepProducing = new MutableBoolean(true);

            Stream<Character> lettersStream = Stream.generate(this::generateRandomCharacter).takeWhile(bol -> isKeepProducing.getValue());
            Stream<Character> producerStream = Stream.concat(lettersStream, Stream.of('\0'));


            producerStream.takeWhile(produce -> isKeepProducing.booleanValue()).forEach(character -> {


                boolean isTerminate = characters.size() % Parameters.LIST_SIZE == 0 && checkIfThresholdAttained(characters);

                if (isTerminate) {

                    LOG.info("We are terminating the character production and will process them.");

                    currentThread().interrupt();
                    isKeepProducing.setFalse();

                    return;
                }

                characters.add(character);

                LOG.info("Producer: " + threadName + " is waiting to transfer...");

                try {

                    Event myEvent = createNewEvent(character);
                    events.add(myEvent);

                    boolean isEventAdded = transferQueue.tryTransfer(myEvent, 4000, TimeUnit.MILLISECONDS);

                    if (isEventAdded) {

                        numberOfProducedMessages.incrementAndGet();
                        LOG.info("Producer: " + threadName + " transferred event with Id " + myEvent.getId());

                    } else {

                        LOG.info("can not add an event due to the timeout");
                    }

                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

            });
        }
    }


    private Event createNewEvent(char ch) {

        Event event = new Event();

        int id = getNumberOfProducedMessages().get() + 1;

        event.setItem(ch);
        event.setDate(new Date());
        event.setId(id);

        return event;
    }

    private char generateRandomCharacter() {

        Random rnd = new Random();

        return (char) ('A' + rnd.nextInt(26));
    }


    /*
     * if the last produced 1000 characters, if 100 of them matches with the condition,
     * we will terminate producer.
     *
     * */
    private boolean checkIfThresholdAttained(List<Character> list) {

        int count = 0;
        boolean result = false;

        for (char character : list) {

            if (
                character == 'A' || character == 'B' || character == 'C' ||
                    character == 'P' || character == 'Q' || character == 'R' ||
                    character == 'X' || character == 'Y' || character == 'Z'
            )

                count++;

            if (count >= Parameters.THRESHOLD) {

                result = true;
                break;
            }
        }

        list.clear();
        return result;
    }

    public AtomicInteger getNumberOfProducedMessages() {

        return numberOfProducedMessages;
    }


}
