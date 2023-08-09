package godsaeng.server.exception.badrequest;

public class DuplicateWeekException extends BadRequestException {

    public DuplicateWeekException() {
        super("주는 중복될 수 없습니다.", 2000);
    }
}
