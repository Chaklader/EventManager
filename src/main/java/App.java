

import exceptions.ParsingException;
import io.vavr.control.Try;
import models.Event;
import processor.EventProcessor;
import stream.ConsumptionManager;
import stream.ProductionManager;
import utils.Parameters;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.function.Supplier;
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


    private static <T> T getTokenValueOrElseThrow(Supplier<T> supplier, String tokenName, Scanner scanner) throws ParsingException {

        int lineNum = 1;
        return Try.ofSupplier(supplier).getOrElseThrow(

            () -> new ParsingException(
                lineNum,
                tokenName,
                scanner.hasNext() ? scanner.next() : "<EOL>"));
    }


    public static void main(String[] args) {


//        String line = "5 > input.txt";
//
//        boolean isMatched = LINE_PATTERN.matcher(line).matches();
//
//
//        if (isMatched) {
//
//            System.out.println("matched");
//        }
//
//        Scanner scanner = new Scanner(line).useDelimiter(DELIMITER);
//
//        while (scanner.hasNext()) {
//
//            int id = getTokenValueOrElseThrow(scanner::nextInt, "", scanner);
//
//            System.out.println("id " + id);
//            String fileLocationb = getTokenValueOrElseThrow(scanner::next, "", scanner);
//
//            System.out.println("location " + fileLocationb);
//        }


        String randomSample = createRandomSample(Parameters.SAMPLE_SIZE);

        LOG.info("Created random sample : " + randomSample);

    }
}
