package godsaeng.server.exception.notfound;

import godsaeng.server.exception.GodsaengException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends GodsaengException {

    public NotFoundException(String message, int code) {
        super(HttpStatus.NOT_FOUND, message, code);
    }
}
