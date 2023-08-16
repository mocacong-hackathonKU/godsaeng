package godsaeng.server.dto.response;

import godsaeng.server.domain.Member;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class GodSaengMemberResponse {
    private String name;
    private String profile;

    public static GodSaengMemberResponse from(Member member) {
        return new GodSaengMemberResponse(member.getNickname(), member.getImgUrl());
    }
}
