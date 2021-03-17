

import exceptions.ParsingException;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import models.Event;
import picocli.CommandLine.Command;
import processor.EventProcessor;
import stream.ConsumptionManager;
import stream.ProductionManager;
import utils.Parameters;

import java.util.regex.Pattern;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static utils.Parameters.DELIMITER;
import static utils.Parameters.LINE_PATTERN;


/**
 * Created by Chaklader on Mar, 2021
 */
@Command
@Slf4j
public class App {


    private static final Logger LOG = Logger.getLogger(App.class.getName());


    private static ProductionManager productionManager;

    private static ConsumptionManager consumptionManager;

    private static String fileLoc;


    static {
            argumentMismatched:

        {
//            String systemProperty = System.getProperty("sun.java.command");
            String systemProperty = "5 < input.txt";


            if (!systemProperty.isEmpty()) {

                boolean isMatched = LINE_PATTERN.matcher(systemProperty).matches();

                if (!isMatched) {

                    log.error("We received program arguments but that doesn't matched with the desired input");

                    break argumentMismatched;
                }


                Scanner scanner = new Scanner(systemProperty).useDelimiter(DELIMITER);

                while (scanner.hasNext()) {

                    int sampleSize = getTokenValueOrElseThrow(scanner::nextInt, "", scanner);
                    Parameters.setSampleSize(sampleSize);

                    String fileLoc = getTokenValueOrElseThrow(scanner::next, "", scanner);

                    System.out.println("He");
                }


            }
        }


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


        if (args == null || args.length == 0 || args[0].isEmpty()) {

            String randomSample = createRandomSample(Parameters.SAMPLE_SIZE);

            LOG.info("Created random sample : " + randomSample);

            return;
        }

        int length = args.length;
        System.out.println(length);

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


    }
}
