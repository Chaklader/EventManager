package stream;


import models.Event;
import org.apache.commons.lang3.mutable.MutableBoolean;
import utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
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

    private final ProducerBreakingConditionImpl producerBreakingCondition;




    public Producer(TransferQueue<Event> transferQueue, String threadName) {

        this.transferQueue = transferQueue;
        this.threadName = threadName;

        this.producerBreakingCondition = new ProducerBreakingConditionImpl();
    }




    @Override
    public void run() {


        synchronized (this) {

            MutableBoolean isKeepProducing = new MutableBoolean(true);

            Stream<Character> lettersStream = Stream.generate(RandomUtils::generateRandomCharacter).takeWhile(isProduceMore -> isKeepProducing.getValue());

            produceCharacterItems(lettersStream, isKeepProducing);
        }
    }


    /**
     * this method will produce the character items for the producer thread
     **/
    private synchronized void produceCharacterItems(Stream<Character> lettersStream, MutableBoolean isKeepProducing) {


        lettersStream.takeWhile(produce -> isKeepProducing.booleanValue()).forEach(character -> {

            boolean isTerminate = producerBreakingCondition.checkProducerBreakingConditions(characters);

            if (isTerminate) {

                LOG.info("We are terminating the character production and will process them.");

                isKeepProducing.setFalse();

                return;
            }

            characters.add(character);

            LOG.info("stream.Producer: " + threadName + " is waiting to transfer...");

            try {

                Event myEvent = Event.createNewEvent(character, getMessagesCount());

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


    public int getMessagesCount() {

        return numberOfProducedMessages.get();
    }

}
