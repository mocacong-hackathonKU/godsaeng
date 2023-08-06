package godsaeng.server.exception.badrequest;

import godsaeng.server.exception.GodsaengException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadRequestException extends GodsaengException {

    public BadRequestException(String message, int code) {
        super(HttpStatus.BAD_REQUEST, message, code);
    }
}
