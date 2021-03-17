

import models.Event;
import processor.EventProcessor;
import stream.ConsumptionManager;
import stream.ProductionManager;
import utils.Parameters;

import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;


/**
 * Created by Chaklader on Mar, 2021
 */
public class App {


    private static final Logger LOG = Logger.getLogger(App.class.getName());



    private static ProductionManager productionManager;

    private static ConsumptionManager consumptionManager;



    static {

        try {
            TransferQueue<Event> transferQueue = new LinkedTransferQueue<>();

            productionManager = new ProductionManager(transferQueue, "producer thread");
            productionManager.start();

            consumptionManager = new ConsumptionManager(productionManager::isAlive, transferQueue, "consumer thread");
            consumptionManager.start();

            productionManager.join();
            consumptionManager.join();

            LOG.info("Transfer completed and consumer/ producer manager terminated their process!");

        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }


    private static String createRandomSample(int randomSampleSize) {

        List<Event> allConsumedEvents = consumptionManager.getAllConsumedEvents();

        EventProcessor processor = new EventProcessor(allConsumedEvents, randomSampleSize);

        String consumedStr = processor.getStringUsingConsumedCharacters();
        String randomSampleRes = processor.createRandomSample(consumedStr);

        LOG.info("Generated random sample from the consumed string ..");

        return randomSampleRes;
    }




    public static void main(String[] args) {


        String randomSample = createRandomSample(Parameters.SAMPLE_SIZE);

        LOG.info("Created random sample : " + randomSample);

    }
}
