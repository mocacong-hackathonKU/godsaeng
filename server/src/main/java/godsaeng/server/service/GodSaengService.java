package godsaeng.server.service;

import godsaeng.server.domain.*;
import godsaeng.server.dto.request.GodSaengSaveRequest;
import godsaeng.server.dto.request.ProofSaveRequest;
import godsaeng.server.dto.response.GodSaengResponse;
import godsaeng.server.dto.response.GodSaengSaveResponse;
import godsaeng.server.dto.response.GodSaengsResponse;
import godsaeng.server.dto.response.ProofSaveResponse;
import godsaeng.server.exception.badrequest.*;
import godsaeng.server.exception.notfound.NotFoundGodSaengException;
import godsaeng.server.exception.notfound.NotFoundMemberException;
import godsaeng.server.repository.*;
import godsaeng.server.support.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GodSaengService {

    private final GodSaengRepository godSaengRepository;
    private final MemberRepository memberRepository;
    private final GodSaengMemberRepository godSaengMemberRepository;
    private final ProofRepopsitory proofRepository;
    private final ProofImageRepository proofImageRepository;
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
    public ProofSaveResponse saveProof(Long memberId, Long godSaengId, MultipartFile proofImg, ProofSaveRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);
        GodSaeng godSaeng = godSaengRepository.findById(godSaengId)
                .orElseThrow(NotFoundGodSaengException::new);
        validateProofCondition(member, godSaeng);

        try {
            String content = request.getProofContent();
            ProofImage proofImage = saveProofImage(godSaengId, proofImg);
            Proof proof = new Proof(content, godSaeng, proofImage, member);
            return new ProofSaveResponse(proofRepository.save(proof).getId());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateProofException();
        }
    }

    private ProofImage saveProofImage(Long godSaengId, MultipartFile proofImg) {
        GodSaeng godSaeng = godSaengRepository.findById(godSaengId)
                .orElseThrow(NotFoundGodSaengException::new);
        if (proofImg == null) {
            throw new NotExistsProofImageException();
        }

        String proofImgUrl = awsS3Uploader.uploadImage(proofImg);
        ProofImage proofImage = new ProofImage(proofImgUrl, godSaeng);
        proofImageRepository.save(proofImage);
        return proofImage;
    }

    private void validateProofCondition(Member member, GodSaeng godSaeng) {
        // TODO: 같생 진행 여부에 따른 검증 코드 추가 필요
//        if (godSaeng.getStatus() != GodSaengStatus.DOING) {
//            throw new InvalidProofStatusException();
//        }
        if (!godSaengMemberRepository.existsByGodSaengAndMember(godSaeng, member)) {
            throw new InvalidProofMemberException();
        }
        validateProofDay(member, godSaeng);
    }

    private void validateProofDay(Member member, GodSaeng godSaeng) {
        LocalDateTime now = LocalDateTime.now();
        // 같은 같생 인증 글 중 사용자가 등록한 가장 최신의 인증글
        Optional<Proof> mostRecentProof = proofRepository
                .findTopByMemberAndGodSaengOrderByCreatedTimeDesc(member, godSaeng);

        if (mostRecentProof.isPresent()) {
            LocalDateTime recentProofCreatedTime = mostRecentProof.get().getCreatedTime().toLocalDateTime();
            // 현재 시간과 가장 최근 갓생 인증글 생성 시간 비교
            if (now.toLocalDate().isEqual(recentProofCreatedTime.toLocalDate())) {
                throw new DuplicateProofException();
            }
        }
    }
}
