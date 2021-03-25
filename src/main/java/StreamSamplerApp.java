

import lombok.extern.slf4j.Slf4j;
import models.Event;
import processor.EventProcessor;
import stream.ConsumptionManager;
import stream.ProductionManager;
import utils.CustomUtils;
import utils.Parameters;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static utils.Parameters.DELIMITER;
import static utils.Parameters.LINE_PATTERN;


/**
 * Created by Chaklader on Mar, 2021
 */
@Slf4j
public class StreamSamplerApp {


    private static final Logger LOG = Logger.getLogger(StreamSamplerApp.class.getName());


    private static ProductionManager productionManager;

    private static ConsumptionManager consumptionManager;


    static {

        boolean isMatched = false;

            argMisMatched:

        /*
         * if we feed the data from the program arguments, read it in this block
         * */
        {
            String systemProperty = System.getProperty("sun.java.command")
                                        .replaceAll(String.valueOf(MethodHandles.lookup().lookupClass().getName()), "")
                                        .replaceAll("\\s+", "");


            if (!systemProperty.isEmpty()) {

                isMatched = LINE_PATTERN.matcher(systemProperty).matches();

                if (!isMatched) {

                    log.error("We received program arguments but that doesn't matched with the desired input");

                    break argMisMatched;
                }

                Scanner scanner = new Scanner(systemProperty).useDelimiter(DELIMITER);

                while (scanner.hasNext()) {

                    int sampleSize = CustomUtils.getTokenValueOrElseThrow(scanner::nextInt, "RANDOM_SAMPLE_SIZE");
                    Parameters.setSampleSize(sampleSize);

                    CustomUtils.setFileLoc(CustomUtils.getTokenValueOrElseThrow(scanner::next, "FILE_LOCATION"));
                }
            }

            isMatched = true;
        }



        /*
         * create threads and perform the consumption/ production procedures
         * */

            argMisMatched:
        {

            if (!isMatched) {

                log.error("We received program arguments but that can't read them due to type/ count mismatched ....");
                break argMisMatched;
            }


            TransferQueue<Event> transferQueue = new LinkedTransferQueue<>();

            productionManager = new ProductionManager(transferQueue, "producer thread");

            consumptionManager = new ConsumptionManager(productionManager::isAlive, transferQueue, "consumer thread");


            try {

                if (CustomUtils.getFileLoc() != null && !CustomUtils.getFileLoc().isEmpty()) {

                    String fileContent = Files.lines(Path.of(CustomUtils.getFileLoc()), StandardCharsets.UTF_8)
                                             .collect(Collectors.joining());

                    char[] chars = fileContent.toCharArray();

                    productionManager.setItemCharsArray(chars);
                }


                productionManager.start();

                consumptionManager.start();


                productionManager.join();

                consumptionManager.join();


                LOG.info("Transfer completed and consumer/ producer manager terminated their process!");

            } catch (InterruptedException | IOException e) {

                e.printStackTrace();
            }
        }

    }


    private static String createRandomSample(int randomSampleSize) {

        List<Event> allConsumedEvents = consumptionManager.getTotalEvents();

        EventProcessor processor = new EventProcessor(allConsumedEvents, randomSampleSize);

        String consumedStr = processor.getStringUsingConsumedCharacters();
        String randomSampleRes = processor.createRandomSample(consumedStr);

        LOG.info("Generated random sample from the consumed string ..");

        return randomSampleRes;
    }


    public static void main(String[] args) {


        LOG.info("we started the program!!!");

        String randomSample = createRandomSample(Parameters.SAMPLE_SIZE);

        LOG.info("Created random sample : " + randomSample);
    }

}
