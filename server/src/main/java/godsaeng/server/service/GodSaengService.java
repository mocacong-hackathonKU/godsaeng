package godsaeng.server.service;

import godsaeng.server.domain.*;
import godsaeng.server.dto.request.GodSaengSaveRequest;
import godsaeng.server.dto.request.ProofSaveRequest;
import godsaeng.server.dto.response.*;
import godsaeng.server.exception.badrequest.*;
import godsaeng.server.exception.notfound.NotFoundGodSaengException;
import godsaeng.server.exception.notfound.NotFoundMemberException;
import godsaeng.server.repository.*;
import godsaeng.server.service.event.DeleteMemberEvent;
import godsaeng.server.support.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GodSaengService {

    private final GodSaengRepository godSaengRepository;
    private final MemberRepository memberRepository;
    private final GodSaengMemberRepository godSaengMemberRepository;
    private final ProofRepository proofRepository;
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

    @Transactional(readOnly = true)
    public MonthlyGodSaengsResponse findMonthlyGodSaeng(Long memberId, LocalDate baseDate) {
        YearMonth baseYearMonth = YearMonth.of(baseDate.getYear(), baseDate.getMonth());

        LocalDate startOfBaseMonth = baseYearMonth.atDay(1);
        LocalDate endOfBaseMonth = baseYearMonth.atEndOfMonth();

        Date startDate = Date.from(startOfBaseMonth.minusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfBaseMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        List<GodSaeng> godSaengs = godSaengRepository.findGodSaengsByBaseTime(memberId, startDate, endDate);

        // 같생 기간이 원하는 달에 포함되는 같생들만 filter
        List<GodSaeng> validGodsaengs =
                getValidMonthlyGodsaengs(startOfBaseMonth, endOfBaseMonth, godSaengs);

        // 같생 날짜가 원하는 달에 포함된 날짜와 상태들만 filter
        List<MonthlyGodSaengResponse> responses =
                getValidMonthlyGodsaengsDate(startOfBaseMonth, endOfBaseMonth, validGodsaengs, memberId);

        // 같생이 겹칠 수 있기 때문에 날짜를 기준으로 그룹화
        Map<LocalDate, List<MonthlyGodSaengResponse>> collect = responses.stream()
                .collect(Collectors.groupingBy(MonthlyGodSaengResponse::getDay, Collectors.toList()));

        List<MonthlyGodSaengResponse> monthlyGodSaengs = new ArrayList<>();

        // 그룹화 했을 때 겹친다면 PROCEEDING을 가장 우선시 해서 넘김
        // 겹칠 때 상태가 다른 경우는 하나만 PROCEEDING인 경우 밖에 없으므로 그 때만 예외처리
        for (LocalDate localDate : collect.keySet()) {
            List<MonthlyGodSaengResponse> monthlyGodSaengResponses = collect.get(localDate);
            List<Boolean> booleans = monthlyGodSaengResponses.stream()
                    .map(MonthlyGodSaengResponse::getIsDone).collect(Collectors.toList());

            if (booleans.stream().anyMatch(bool -> bool.equals(false))) {
                monthlyGodSaengs.add(new MonthlyGodSaengResponse(localDate, false));
                continue;
            }
            monthlyGodSaengs.add(new MonthlyGodSaengResponse(localDate, true));
        }

        monthlyGodSaengs.sort(Comparator.comparing(MonthlyGodSaengResponse::getDay));

        return new MonthlyGodSaengsResponse(monthlyGodSaengs);
    }

    @Transactional(readOnly = true)
    public DailyGodSaengsResponse findDailyGodSaeng(Long memberId, LocalDate baseDate) {
        Member member = memberRepository.findById(memberId).orElseThrow(NotFoundMemberException::new);

        YearMonth baseYearMonth = YearMonth.of(baseDate.getYear(), baseDate.getMonth());

        LocalDate startOfBaseMonth = baseYearMonth.atDay(1);
        LocalDate endOfBaseMonth = baseYearMonth.atEndOfMonth();

        Date startDate = Date.from(startOfBaseMonth.atStartOfDay().minusMonths(1).atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfBaseMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        List<GodSaeng> godSaengs = godSaengRepository.findGodSaengsByBaseTime(memberId, startDate, endDate);

        List<Proof> proofs = proofRepository.findProofWithGodSaengByMemberId(memberId,
                Date.from(baseDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(baseDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
        );

        // 같생 목록을 DailyGodSaengResponse로 변환하여 리스트에 추가
        List<DailyGodSaengResponse> dailyResponses = godSaengs.stream()
                // 해당 날짜 요일의 같생 목록 필터링
                .filter(godSaeng -> getValidDailyGodSaeng(godSaeng, baseDate))
                .map(godSaeng -> new DailyGodSaengResponse(
                        godSaeng.getId(),
                        godSaeng.getTitle(),
                        godSaeng.getStatus(),
                        proofs.stream().anyMatch(proof -> proof.isSameGodSaeng(godSaeng))))
                .collect(Collectors.toList());

        return new DailyGodSaengsResponse(dailyResponses);
    }

    private List<MonthlyGodSaengResponse> getValidMonthlyGodsaengsDate(LocalDate startOfBaseMonth,
                                                                       LocalDate endOfBaseMonth,
                                                                       List<GodSaeng> validGodsaengs,
                                                                       Long memberId) {
        return validGodsaengs.stream()
                .flatMap(validGodsaeng ->
                        validGodsaeng.getDoingDate().stream()
                                .filter(date -> date.isAfter(startOfBaseMonth.minusDays(1)))
                                .filter(date -> date.isBefore(endOfBaseMonth.plusDays(1)))
                                .map(localDate -> {
                                    if (localDate.isBefore(LocalDate.now().plusDays(1))) {
                                        List<Proof> proofs = proofRepository.findProofWithGodSaengByMemberId(
                                                memberId,
                                                Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                                                Date.from(localDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

                                        boolean isDone = proofs.stream().anyMatch(proof -> proof.isSameGodSaeng(validGodsaeng));
                                        return new MonthlyGodSaengResponse(localDate, isDone);
                                    }
                                    return new MonthlyGodSaengResponse(localDate, false);

                                })).collect(Collectors.toList());
    }

    private List<GodSaeng> getValidMonthlyGodsaengs(LocalDate startOfBaseMonth,
                                                    LocalDate endOfBaseMonth, List<GodSaeng> godSaengs) {
        return godSaengs.stream()
                .filter(godSaeng -> godSaeng.getClosedDate().isAfter(startOfBaseMonth))
                .filter(godSaeng -> godSaeng.getOpenedDate().isBefore(endOfBaseMonth))
                .collect(Collectors.toList());
    }

    private boolean getValidDailyGodSaeng(GodSaeng godSaeng, LocalDate baseDate) {
        List<LocalDate> doingDate = godSaeng.getDoingDate();

        return doingDate.stream().anyMatch(date -> date.isEqual(baseDate));
    }

    @Transactional(readOnly = true)
    public GodSaengDetailResponse findGodSaengDetail(Long memberId, Long godSaengId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);
        GodSaeng godSaeng = godSaengRepository.findById(godSaengId).orElseThrow(NotFoundGodSaengException::new);
        List<Member> members = memberRepository.findMembersByGodSaengId(godSaengId);
        List<Proof> proofs = proofRepository.findProofsWithMemberByGodSaengId(godSaengId);

        boolean isJoined = members.stream().anyMatch(user -> user.getId().equals(member.getId()));

        int progress = getProgress(godSaeng, members, proofs);

        return GodSaengDetailResponse.from(godSaeng, members, proofs, progress, isJoined);
    }

    private int getProgress(GodSaeng godSaeng, List<Member> members, List<Proof> proofs) {
        return (proofs.size() * 100) / (members.size() * GodSaeng.DOING_WEEKS * godSaeng.getWeeks().size());
    }

    @Transactional
    public ProofSaveResponse saveProof(Long memberId, Long godSaengId, MultipartFile proofImg,
                                       ProofSaveRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);
        GodSaeng godSaeng = godSaengRepository.findById(godSaengId)
                .orElseThrow(NotFoundGodSaengException::new);
        validateProofCondition(member, godSaeng);

        try {
            String content = request.getContent();
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
        ProofImage proofImage = new ProofImage(proofImgUrl);
        proofImageRepository.save(proofImage);
        return proofImage;
    }

    private void validateProofCondition(Member member, GodSaeng godSaeng) {
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

    @EventListener
    public void updateGodSaengWhenMemberDelete(DeleteMemberEvent event) {
        Long memberId = event.getMember().getId();
        godSaengRepository.findAllByOwnerId(memberId)
                .forEach(GodSaeng::removeOwner);
        godSaengMemberRepository.findAllGodSaengMemberByMemberId(memberId)
                .forEach(GodSaengMember::removeMember);
        proofRepository.deleteAllByMemberId(memberId);
    }
}
