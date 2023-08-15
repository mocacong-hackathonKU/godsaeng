package godsaeng.server.exception.badrequest;

public class InvalidProofMemberException extends BadRequestException {

    public InvalidProofMemberException() {
        super("해당 같생에 참여한 회원이 아닙니다.", 3002);
    }
}
