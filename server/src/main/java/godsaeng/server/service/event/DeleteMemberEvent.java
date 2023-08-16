package godsaeng.server.service.event;

import godsaeng.server.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DeleteMemberEvent {

    private final Member member;
}
