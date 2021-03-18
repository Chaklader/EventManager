package exceptions;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {


    protected final String tokenName;

    public BaseException(String message, String tokenName) {

        super(message);
        this.tokenName = tokenName;
    }

}
