package godsaeng.server.exception.badrequest;

public class InvalidProofStatusException extends BadRequestException {

    public InvalidProofStatusException() {
        super("인증 기간이 올바르지 않습니다.", 3003);
    }
}
