package utils;

import java.util.regex.Pattern;

/**
 * Created by Chaklader on Mar, 2021
 */
public class Parameters {


    public static int LIST_SIZE = 1000;

    public static int THRESHOLD = 100;

    public static int PRODUCER_TIMEOUT_LIMIT = 4000;


    public static int SAMPLE_SIZE = 5;


    public static Pattern LINE_PATTERN = Pattern.compile("\\d<.*\\.txt");

    public static Pattern DELIMITER = Pattern.compile("<");

    public static Pattern DATE_TIME_FORMAT = Pattern.compile("\\d{4}.\\d{2}.\\d{2}.\\d{2}.\\d{2}.\\d{2}");

    public static int getSampleSize() {
        return SAMPLE_SIZE;
    }

    public static void setSampleSize(int sampleSize) {
        SAMPLE_SIZE = sampleSize;
    }
}
