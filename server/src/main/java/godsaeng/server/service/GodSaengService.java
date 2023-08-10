package godsaeng.server.service;

import godsaeng.server.domain.*;
import godsaeng.server.dto.request.GodSaengSaveRequest;
import godsaeng.server.dto.response.GodSaengResponse;
import godsaeng.server.dto.response.GodSaengSaveResponse;
import godsaeng.server.dto.response.GodSaengsResponse;
import godsaeng.server.exception.badrequest.DuplicateGodSaengException;
import godsaeng.server.exception.badrequest.DuplicateWeekException;
import godsaeng.server.exception.badrequest.NotExistsProofImageException;
import godsaeng.server.exception.notfound.NotFoundGodSaengException;
import godsaeng.server.exception.notfound.NotFoundMemberException;
import godsaeng.server.repository.GodSaengMemberRepository;
import godsaeng.server.repository.GodSaengRepository;
import godsaeng.server.repository.MemberRepository;
import godsaeng.server.repository.ProofRepopsitory;
import godsaeng.server.support.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GodSaengService {

    private final GodSaengRepository godSaengRepository;
    private final MemberRepository memberRepository;
    private final GodSaengMemberRepository godSaengMemberRepository;
    private final ProofRepopsitory proofRepopsitory;
    private final AwsS3Uploader awsS3Uploader;

    @Transactional
    public GodSaengSaveResponse save(Long memberId, GodSaengSaveRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);
        GodSaeng godSaeng = new GodSaeng(
                request.getTitle(),
                request.getDescription(),
                member);
        godSaeng.addAllWeek(request.getWeeks());
        try {
            return new GodSaengSaveResponse(godSaengRepository.save(godSaeng).getId());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateWeekException();
        }
    }

    @Transactional(readOnly = true)
    public GodSaengsResponse findAllGodSaeng() {
        List<GodSaengResponse> godSaengResponses = godSaengRepository.findAll().stream()
                .map(godSaeng -> new GodSaengResponse(
                                godSaeng.getId(),
                                godSaeng.getTitle(),
                                godSaeng.getDescription(),
                                godSaeng.getWeeks().stream().map(GodSaengWeek::getWeek).collect(Collectors.toList())))
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
            godSaengMemberRepository.save(new GodSaengMember(godSaeng, member));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateGodSaengException();
        }
    }

    @Transactional
    public void saveProof(Long memberId, Long godSaengId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);
        GodSaeng godSaeng = godSaengRepository.findById(godSaengId)
                .orElseThrow(NotFoundGodSaengException::new);

        try {

        } catch (DataIntegrityViolationException e) {
            throw new DuplicateGodSaengException();
        }
    }

    @Transactional
    public void saveProofImage(Long memberId, Long godSaengId, MultipartFile proofImg) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);
        GodSaeng godSaeng = godSaengRepository.findById(godSaengId)
                .orElseThrow(NotFoundGodSaengException::new);

        if (proofImg == null) {
            throw new NotExistsProofImageException();
        }

        String proofImgUrl = awsS3Uploader.uploadImage(proofImg);
        ProofImage proofImage = new ProofImage(proofImgUrl, godSaeng);
        Proof proof = new Proof()
        godSaengRepository.save(memberProfileImage);
        member.updateProfileImgUrl(memberProfileImage);
    }
}
