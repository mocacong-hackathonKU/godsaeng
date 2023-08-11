package godsaeng.server.exception.badrequest;

public class DuplicateProofException extends BadRequestException {

    public DuplicateProofException() {
        super("이미 등록한 인증입니다.", 3001);
    }
}
