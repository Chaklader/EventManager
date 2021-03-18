package exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ParsingException extends BaseException {

    private final String tokenName;

    public ParsingException(String tokenName) {

        super("Invalid token found at the time of parsing with type of: ", tokenName);

        this.tokenName = tokenName;
    }


}
