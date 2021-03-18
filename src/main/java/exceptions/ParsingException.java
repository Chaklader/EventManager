package exceptions;


import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ParsingException extends RuntimeException {


    public ParsingException(String tokenName) {

        super("Invalid token found at the time of parsing with type of: " + tokenName);
    }

}
