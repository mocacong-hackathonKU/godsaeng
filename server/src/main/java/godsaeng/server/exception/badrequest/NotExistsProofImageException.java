package godsaeng.server.exception.badrequest;

public class NotExistsProofImageException extends BadRequestException {

    public NotExistsProofImageException() {
        super("등록하려는 인증 사진이 없습니다.", 3000);
    }
}
