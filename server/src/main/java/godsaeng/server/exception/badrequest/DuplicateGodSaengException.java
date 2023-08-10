package godsaeng.server.exception.badrequest;

public class DuplicateGodSaengException extends BadRequestException {

    public DuplicateGodSaengException() {
        super("이미 등록한 갓생입니다.", 2003);
    }
}
