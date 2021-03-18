package utils;

import exceptions.ParsingException;
import io.vavr.control.Try;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Created by Chaklader on Mar, 2021
 */
public class CustomUtils {


    private static final Random rnd = new Random();


    public static char generateRandomCharacter() {

        return (char) ('A' + rnd.nextInt(26));
    }


    public static boolean isLetterOrBlank(char c) {

        return (c >= 'A' && c <= 'Z') || c == '\0';
    }

    public static <T> T getTokenValueOrElseThrow(Supplier<T> supplier, String tokenType) throws ParsingException {

        return Try.ofSupplier(supplier).getOrElseThrow(

            () -> new ParsingException(tokenType)
        );
    }

}
