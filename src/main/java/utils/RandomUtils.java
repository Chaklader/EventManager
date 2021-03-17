package utils;

import java.util.Random;

/**
 * Created by Chaklader on Mar, 2021
 */
public class RandomUtils {


    private static final Random rnd = new Random();


    public static char generateRandomCharacter() {

        return (char) ('A' + rnd.nextInt(26));
    }
}
