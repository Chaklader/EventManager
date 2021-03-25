package stream;


import com.google.common.primitives.Chars;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import models.Event;
import org.apache.commons.lang3.mutable.MutableBoolean;
import utils.CustomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static utils.Parameters.PRODUCER_TIMEOUT_LIMIT;


@Slf4j
@Data
public class ProductionManager extends Thread {


    private final TransferQueue<Event> transferQueue;

    private final String threadName;

    private final AtomicInteger numberOfProducedMessages = new AtomicInteger();

    protected final List<Character> tempStorage = new ArrayList<>();

    private final ProducerTerminationManager producerBreakingCondition;

    private final Producer producer;


    // check if the stream should produce more items
    MutableBoolean isKeepProducing = new MutableBoolean(true);

    char[] itemCharsArray;


    public ProductionManager(TransferQueue<Event> transferQueue, String threadName) {

        this.transferQueue = transferQueue;
        this.threadName = threadName;

        this.producerBreakingCondition = new ProducerTerminationManager();

        this.producer = new Producer();
    }


    @Override
    public void run() {


        synchronized (this) {

            Stream<Character> lettersStream = createCharacterItemStream();

            producer.produceCharacterItems(lettersStream, isKeepProducing);
        }
    }

    public Stream<Character> createCharacterItemStream() {

        if (itemCharsArray == null || itemCharsArray.length == 0) {

            return Stream.generate(CustomUtils::generateCharacterItems).takeWhile(isProduceMore -> isKeepProducing.getValue());
        }

        Stream<Character> characterStream = Chars.asList(itemCharsArray).stream().takeWhile(s -> isKeepProducing.getValue());

        return Stream.concat(characterStream, Stream.of('\0'));
    }


    private class Producer {


        /**
         * this method will produce the character items for the producer thread
         **/
        private synchronized void produceCharacterItems(Stream<Character> lettersStream, MutableBoolean isKeepProducing) {


            io.vavr.collection.Stream.ofAll(lettersStream).zipWithIndex()
                .filter(character -> CustomUtils.isLetterOrBlank(character._1()))
                .forEach(

                    itemNumAndChar -> {


                        char cItem = itemNumAndChar._1();

                        int msgId = itemNumAndChar._2() + 1;


//                        boolean isTerminate = producerBreakingCondition.checkProducerBreakingConditions(tempStorage);


                        if (cItem == '\0') {

                            log.info("We are terminating the character production and will process them.");

                            isKeepProducing.setFalse();

                            return;
                        }


                        tempStorage.add(cItem);

                        log.info("stream.Producer: " + threadName + " is waiting to transfer...");

                        try {

                            Event myEvent = Event.createNewEvent(cItem, msgId);

                            boolean isEventAdded = transferQueue.tryTransfer(myEvent, PRODUCER_TIMEOUT_LIMIT, TimeUnit.MILLISECONDS);

                            if (isEventAdded) {

                                numberOfProducedMessages.incrementAndGet();
                                log.info("Producer: " + threadName + " transferred event with Id " + myEvent.getId());

                            } else {

                                log.info("can not add an event due to the timeout");
                            }

                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                    }
                );

        }

    }

}
