package godsaeng.server.exception.unauthorized;

import godsaeng.server.exception.GodsaengException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends GodsaengException {

    public UnauthorizedException(String message, int code) {
        super(HttpStatus.UNAUTHORIZED, message, code);
    }
}
