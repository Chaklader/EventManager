package stream;


import lombok.extern.slf4j.Slf4j;
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



@Slf4j
public class ProductionManager extends Thread {



    private final TransferQueue<Event> transferQueue;

    private final String threadName;

    private final AtomicInteger numberOfProducedMessages = new AtomicInteger();

    private final List<Character> characters = new ArrayList<>();

    private final ProducerBreakingConditionImpl producerBreakingCondition;

    private final Producer producer;


    public ProductionManager(TransferQueue<Event> transferQueue, String threadName) {

        this.transferQueue = transferQueue;
        this.threadName = threadName;

        this.producerBreakingCondition = new ProducerBreakingConditionImpl();

        this.producer = new Producer();
    }


    @Override
    public void run() {


        synchronized (this) {

            MutableBoolean isKeepProducing = new MutableBoolean(true);

            Stream<Character> generatedStream = Stream.generate(RandomUtils::generateRandomCharacter).takeWhile(isProduceMore -> isKeepProducing.getValue());
            Stream<Character> lettersStream = Stream.concat(generatedStream, Stream.of('\0'));

            producer.produceCharacterItems(lettersStream, isKeepProducing);
        }
    }


    public int getMessagesCount() {

        return numberOfProducedMessages.get();
    }


    private class Producer {


        /**
         * this method will produce the character items for the producer thread
         **/
        private synchronized void produceCharacterItems(Stream<Character> lettersStream, MutableBoolean isKeepProducing) {


            lettersStream.takeWhile(produce -> isKeepProducing.booleanValue()).forEach(character -> {

                boolean isTerminate = producerBreakingCondition.checkProducerBreakingConditions(characters);

                if (isTerminate) {

                    log.info("We are terminating the character production and will process them.");

                    isKeepProducing.setFalse();

                    return;
                }

                characters.add(character);

                log.info("stream.Producer: " + threadName + " is waiting to transfer...");

                try {

                    Event myEvent = Event.createNewEvent(character, getMessagesCount());

                    boolean isEventAdded = transferQueue.tryTransfer(myEvent, 4000, TimeUnit.MILLISECONDS);

                    if (isEventAdded) {

                        numberOfProducedMessages.incrementAndGet();
                        log.info("Producer: " + threadName + " transferred event with Id " + myEvent.getId());

                    } else {

                        log.info("can not add an event due to the timeout");
                    }

                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

            });
        }

    }

}
