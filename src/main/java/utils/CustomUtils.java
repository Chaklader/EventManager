package utils;

import exceptions.ParsingException;
import io.vavr.Lazy;
import io.vavr.control.Try;
import net.andreinc.mockneat.MockNeat;
import org.apache.commons.lang3.RandomUtils;

import java.util.function.Supplier;

import static io.vavr.API.$;
import static io.vavr.API.Case;


/**
 * Created by Chaklader on Mar, 2021
 */
public class CustomUtils {


    public static String fileLoc;


    public static char generateRandomCharacter() {

        return (char) ('A' + RandomUtils.nextInt(0, 26));
    }

    private static Lazy<Character> createCharacterLazy() {

        Lazy<Character> of = Lazy.of(CustomUtils::generateRandomCharacter);

        return of;
    }


    public static boolean isLetterOrBlank(char c) {

        return (c >= 'A' && c <= 'Z') || c == '\0';
    }

    public static <T> T getTokenValueOrElseThrow(Supplier<T> supplier, String tokenName) throws ParsingException {

        return Try.ofSupplier(supplier).getOrElseThrow(

            () -> new ParsingException(tokenName)
        );
    }


    public static char generateCharacterItems() {

        MockNeat mockNeat = MockNeat.secure();

        String s = mockNeat.probabilites(String.class)
                       .add(0.9, "NORMAL_CHARACTER") // 90% chance to pick A
                       .add(0.1, "SPECIAL_CHARACTER") // 10% chance to pick B
                       .val();


        char output = io.vavr.API.Match(s).of(

            Case($("NORMAL_CHARACTER"), createCharacterLazy().getOrElse('\0')),

            Case($("SPECIAL_CHARACTER"), '\0'),

            Case($(), '\0'));


        return output;
    }

    public static String getFileLoc() {
        return fileLoc;
    }

    public static void setFileLoc(String fileLoc) {
        CustomUtils.fileLoc = fileLoc;
    }

}
