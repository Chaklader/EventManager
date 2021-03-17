package exceptions;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    protected final int lineNumber;

    public BaseException(String message, int lineNumber) {

        super(message);
        this.lineNumber = lineNumber;
    }
}
