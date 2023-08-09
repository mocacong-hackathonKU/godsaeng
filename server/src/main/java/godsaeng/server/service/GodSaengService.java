package godsaeng.server.service;

import godsaeng.server.domain.GodSaeng;
import godsaeng.server.domain.Member;
import godsaeng.server.dto.request.GodSaengSaveRequest;
import godsaeng.server.dto.response.GodSaengResponse;
import godsaeng.server.dto.response.GodSaengSaveResponse;
import godsaeng.server.dto.response.GodSaengsResponse;
import godsaeng.server.exception.badrequest.DuplicateGodSaengException;
import godsaeng.server.exception.notfound.NotFoundGodSaengException;
import godsaeng.server.exception.notfound.NotFoundMemberException;
import godsaeng.server.repository.GodSaengRepository;
import godsaeng.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GodSaengService {

    private final GodSaengRepository godSaengRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public GodSaengSaveResponse save(Long memberId, GodSaengSaveRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);
        GodSaeng godSaeng = new GodSaeng(request.getTitle(), request.getDescription(), request.getWeeks(), member);
        return new GodSaengSaveResponse(godSaengRepository.save(godSaeng).getId());
    }

    @Transactional(readOnly = true)
    public GodSaengsResponse findAllGodSaeng() {
        List<GodSaengResponse> godSaengResponses = godSaengRepository.findAll().stream()
                .map(godSaeng -> new GodSaengResponse(
                                godSaeng.getId(),
                                godSaeng.getTitle(),
                                godSaeng.getDescription(),
                                godSaeng.getWeeks()))
                .collect(Collectors.toList());
        return new GodSaengsResponse(godSaengResponses);
    }

    @Transactional
    public void attendGodSaeng(Long memberId, Long godSaengId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);
        GodSaeng godSaeng = godSaengRepository.findById(godSaengId)
                .orElseThrow(NotFoundGodSaengException::new);

        try {
            godSaeng.addMember(member);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateGodSaengException();
        }
    }

}
