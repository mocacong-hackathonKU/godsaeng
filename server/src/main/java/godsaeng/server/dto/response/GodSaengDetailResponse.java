package godsaeng.server.dto.response;

import godsaeng.server.domain.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class GodSaengDetailResponse {
    private String title;
    private String description;
    private List<Week> weeks;
    private LocalDate openDate;
    private LocalDate closeDate;
    private List<GodSaengMemberResponse> members;
    private int progress;
    private GodSaengStatus status;
    private Boolean isJoined;
    private List<ProofResponse> proofs;

    public static GodSaengDetailResponse from(GodSaeng godSaeng, List<Member> members, List<Proof> proofs, int progress, boolean isJoined) {
        return new GodSaengDetailResponse(
                godSaeng.getTitle(),
                godSaeng.getDescription(),
                godSaeng.getWeeks().stream().map(GodSaengWeek::getWeek).collect(Collectors.toList()),
                godSaeng.getOpenedDate(),
                godSaeng.getClosedDate(),
                members.stream().map(GodSaengMemberResponse::from).collect(Collectors.toList()),
                progress,
                godSaeng.getStatus(),
                isJoined,
                proofs.stream().map(ProofResponse::from).collect(Collectors.toList())
        );
    }
}
