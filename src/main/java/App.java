

import lombok.extern.slf4j.Slf4j;
import models.Event;
import picocli.CommandLine.Command;
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
@Command
@Slf4j
public class App {


    private static final Logger LOG = Logger.getLogger(App.class.getName());


    private static ProductionManager productionManager;

    private static ConsumptionManager consumptionManager;

    private static String fileLoc;


    static {


        boolean isMatched = false;

            argMisMatched:

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

                    int sampleSize = CustomUtils.getTokenValueOrElseThrow(scanner::nextInt, "SAMPLE_SIZE");
                    Parameters.setSampleSize(sampleSize);

                    String fileLoc = CustomUtils.getTokenValueOrElseThrow(scanner::next, "FILE_LOCATION");
                    setFileLoc(fileLoc);
                }
            }

            isMatched = true;
        }


            label:
        {


            if (!isMatched) {

                break label;
            }


            TransferQueue<Event> transferQueue = new LinkedTransferQueue<>();

            productionManager = new ProductionManager(transferQueue, "producer thread");
            consumptionManager = new ConsumptionManager(productionManager::isAlive, transferQueue, "consumer thread");

            try {


                if (fileLoc != null && !fileLoc.isEmpty()) {

                    char[] chars = Files.lines(Path.of(fileLoc), StandardCharsets.UTF_8).collect(Collectors.joining()).toCharArray();

                    productionManager.setChgar(chars);

//                    Stream<Character> chs = Chars.asList(chars).stream().takeWhile(s -> productionManager.getIsKeepProducing().getValue());

                    productionManager.setReadArgs(true);
//                    productionManager.setCharactersStream(chs);

                    System.out.println(fileLoc);
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

    public static String getFileLoc() {
        return fileLoc;
    }

    public static void setFileLoc(String fileLoc) {
        App.fileLoc = fileLoc;
    }
}
