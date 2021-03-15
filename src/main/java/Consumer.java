import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Consumer implements Runnable {


    final List<Event> generated = new ArrayList<>();

    private static final Logger LOG = Logger.getLogger(Consumer.class.getName());

    private final TransferQueue<Event> transferQueue;

    private final String name;

    final int numberOfMessagesToConsume;

    final AtomicInteger numberOfConsumedMessages = new AtomicInteger();

    final List<Event> events = new ArrayList<>();


    public List<Event> getGenerated() {
        return generated;
    }

//    final Object obj;

    Consumer(TransferQueue<Event> transferQueue, String name, int numberOfMessagesToConsume) {

        this.transferQueue = transferQueue;
        this.name = name;
        this.numberOfMessagesToConsume = numberOfMessagesToConsume;

//        this.obj = obj;
    }

    Event element;

    @Override
    public void run() {

        synchronized (events) {

            while (true){

                try {
                    LOG.info("Consumer: " + name + " is waiting to take element...");

                    element = transferQueue.take();

                    longProcessing(element);

                    System.out.println("Consumer: " + name + " received element with messgae : " + element.getC());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }

    }

    private void longProcessing(Event element) throws InterruptedException {

        numberOfConsumedMessages.incrementAndGet();
        Thread.sleep(5);
    }


//    public void consume(Event e) throws InterruptedException {
//
//        synchronized (generated) {
//            while (generated.isEmpty() && !Parameters.producerFinished) // If list is empty then will have to wait
//            {
//                System.out.println("List is empty " + Thread.currentThread().getName() + " Is waiting and " + "Size is " + generated.size());
//                generated.wait();
//            }
//
//            if (!generated.isEmpty()) {
//                generated.add(e);
//                //consume element
//            } else {
//                //List is empty and producer has finished.
//                System.out.println("Consume process is finished");
//            }
//        }
//
//        synchronized (generated) {
//            generated.notifyAll();
//            generated.remove(0);
//        }
//
//    }

}
