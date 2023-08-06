package godsaeng.server.exception.unauthorized;

public class InvalidTokenException extends UnauthorizedException {

    public InvalidTokenException() {
        super("올바르지 않은 토큰입니다. 다시 로그인해주세요.", 1005);
    }

    public InvalidTokenException(String message) {
        super(message, 1005);
    }
}
