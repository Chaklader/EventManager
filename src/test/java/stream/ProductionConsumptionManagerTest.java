package stream;

import models.Event;
import org.junit.jupiter.api.Test;
import utils.CustomUtils;
import utils.Parameters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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


        Parameters.setSampleSize(5);
        CustomUtils.setFileLoc("src/main/resources/input.txt");

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

        assertEquals(producedSmg, consumedMsg + 1);
    }


}