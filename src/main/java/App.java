

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


    public static void main(String[] args) throws InterruptedException {


        TransferQueue<Event> transferQueue = new LinkedTransferQueue<>();

        ProductionManager productionManager = new ProductionManager(transferQueue, "producer thread");
        productionManager.start();

        ConsumptionManager consumptionManager = new ConsumptionManager(productionManager::isAlive, transferQueue, "consumer thread");
        consumptionManager.start();

        productionManager.join();
        consumptionManager.join();

        LOG.info("Consumer and producer threads are termination and we will create the random sample from the generated string...");

        List<Event> allConsumedEvents = consumptionManager.getAllConsumedEvents();

        EventProcessor processor = new EventProcessor(allConsumedEvents, Parameters.SAMPLE_SIZE);

        String consumedStr = processor.getStringUsingConsumedCharacters();
        String randomSampleRes = processor.createRandomSample(consumedStr);


        LOG.info("Generated random sample from the consumed string: " + randomSampleRes);

        LOG.info("Transfer completed");
    }
}
