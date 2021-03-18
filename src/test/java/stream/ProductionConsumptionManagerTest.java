package stream;

import models.Event;
import org.junit.jupiter.api.Test;
import processor.EventProcessor;
import utils.CustomUtils;
import utils.Parameters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Created by Chaklader on Mar, 2021
 */
class ProductionConsumptionManagerTest {



    @Test
    public void whenUseOneProducerAndNoConsumers_thenShouldFailWithTimeout() throws InterruptedException {

        TransferQueue<Event> transferQueue = new LinkedTransferQueue<>();
        ExecutorService exService = Executors.newFixedThreadPool(2);

        ProductionManager productionManager = new ProductionManager(transferQueue, "producer thread");

        exService.execute(productionManager);

        boolean isTerminated = exService.awaitTermination(5000, TimeUnit.MILLISECONDS);
        exService.shutdown();

        assertEquals(productionManager.getNumberOfProducedMessages().intValue(), 0);
    }


    @Test
    public void whenUseOneConsumerAndOneProducer_thenShouldProcessAllMessages() throws InterruptedException {

        TransferQueue<Event> transferQueue = new LinkedTransferQueue<>();

        ProductionManager productionManager = new ProductionManager(transferQueue, "producer thread");
        productionManager.start();

        ConsumptionManager consumptionManager = new ConsumptionManager(productionManager::isAlive, transferQueue, "consumer thread");
        consumptionManager.start();


        productionManager.join();
        consumptionManager.join();

        int producedSmg = productionManager.getNumberOfProducedMessages().intValue();
        int consumedMsg = consumptionManager.getNumberOfConsumedMessages().intValue();

        assertEquals(producedSmg, consumedMsg + 1);
    }


    @Test
    public void whenUseOneConsumerAndOneProducer_thenShouldProcessAllMessages_ReadFileData() throws InterruptedException, IOException {


        final int SAMPLE_SIZE = 5;
        final String FILE_LOCATION = "src/main/resources/input.txt";

        Parameters.setSampleSize(SAMPLE_SIZE);
        CustomUtils.setFileLoc(FILE_LOCATION);


        String fileContent = Files.lines(Path.of(CustomUtils.getFileLoc()), StandardCharsets.UTF_8)
                                 .collect(Collectors.joining());

        char[] chars = fileContent.toCharArray();


        TransferQueue<Event> transferQueue = new LinkedTransferQueue<>();

        ProductionManager productionManager = new ProductionManager(transferQueue, "producer thread");
        productionManager.setItemCharsArray(chars);
        productionManager.start();


        ConsumptionManager consumptionManager = new ConsumptionManager(productionManager::isAlive, transferQueue, "consumer thread");
        consumptionManager.start();


        productionManager.join();
        consumptionManager.join();


        int producedSmg = productionManager.getNumberOfProducedMessages().intValue();
        int consumedMsg = consumptionManager.getNumberOfConsumedMessages().intValue();


        List<Event> totalEvents = consumptionManager.getTotalEvents();

        EventProcessor processor = new EventProcessor(totalEvents, SAMPLE_SIZE);

        String consumedStr = processor.getStringUsingConsumedCharacters();

        byte[] decodedBytes = Base64.getDecoder().decode(consumedStr);
        String decodedString = new String(decodedBytes);

        String randomSampleRes = processor.createRandomSample(consumedStr);


        // assertion checks
        assertEquals(producedSmg, consumedMsg + 1);

        assertEquals(consumedMsg, decodedString.length());

        assertEquals(5, randomSampleRes.length());
    }


}