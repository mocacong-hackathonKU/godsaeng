package godsaeng.server.exception.notfound;

import lombok.Getter;

@Getter
public class NotFoundGodSaengException extends NotFoundException {

    public NotFoundGodSaengException() {
        super("존재하지 않는 갓생입니다.", 2002);
    }
}
