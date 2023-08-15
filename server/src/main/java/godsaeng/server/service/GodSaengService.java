package godsaeng.server.service;

import godsaeng.server.domain.*;
import godsaeng.server.dto.request.GodSaengSaveRequest;
import godsaeng.server.dto.response.*;
import godsaeng.server.exception.badrequest.DuplicateGodSaengException;
import godsaeng.server.exception.badrequest.DuplicateWeekException;
import godsaeng.server.exception.notfound.NotFoundGodSaengException;
import godsaeng.server.exception.notfound.NotFoundMemberException;
import godsaeng.server.repository.GodSaengMemberRepository;
import godsaeng.server.repository.GodSaengRepository;
import godsaeng.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GodSaengService {

    private final GodSaengRepository godSaengRepository;
    private final MemberRepository memberRepository;
    private final GodSaengMemberRepository godSaengMemberRepository;

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

        Date startDate = Date.from(startOfBaseMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfBaseMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        List<GodSaeng> godSaengs = godSaengRepository.findGodSaengsByBaseTime(memberId, startDate, endDate);

        // 같생 기간이 원하는 달에 포함되는 같생들만 filter
        List<GodSaeng> validGodsaengs =
                getValidGodsaengs(startOfBaseMonth, endOfBaseMonth, godSaengs);

        // 같생 날짜가 원하는 달에 포함된 날짜와 상태들만 filter
        List<MonthlyGodSaengResponse> responses =
                getValidGodsaengsDate(startOfBaseMonth, endOfBaseMonth, validGodsaengs);

        // 같생이 겹칠 수 있기 때문에 날짜를 기준으로 그룹화
        Map<LocalDate, List<MonthlyGodSaengResponse>> collect = responses.stream()
                .collect(Collectors.groupingBy(MonthlyGodSaengResponse::getDay, Collectors.toList()));

        List<MonthlyGodSaengResponse> monthlyGodSaengResponse = new ArrayList<>();

        // 그룹화 했을 때 겹친다면 PROCEEDING을 가장 우선시 해서 넘김
        // 겹칠 때 상태가 다른 경우는 하나만 PROCEEDING인 경우 밖에 없으므로 그 때만 예외처리
        for (LocalDate localDate : collect.keySet()) {
            List<MonthlyGodSaengResponse> monthlyGodSaengResponses = collect.get(localDate);
            List<GodSaengStatus> statuses = monthlyGodSaengResponses.stream()
                    .map(MonthlyGodSaengResponse::getStauts).collect(Collectors.toList());

            if (hasProceedGodSaeng(statuses)) {
                monthlyGodSaengResponse.add(new MonthlyGodSaengResponse(localDate, GodSaengStatus.PROCEEDING));
                continue;
            }
            monthlyGodSaengResponse.add(monthlyGodSaengResponses.get(0));
        }
        return new MonthlyGodSaengsResponse(monthlyGodSaengResponse);
    }

    private boolean hasProceedGodSaeng(List<GodSaengStatus> statuses) {
        return statuses.size() > 1 && statuses.contains(GodSaengStatus.PROCEEDING);
    }

    private List<MonthlyGodSaengResponse> getValidGodsaengsDate(LocalDate startOfBaseMonth, LocalDate endOfBaseMonth, List<GodSaeng> validGodsaengs) {
        return validGodsaengs.stream()
                .flatMap(validGodsaeng ->
                        validGodsaeng.getDoingDate().stream()
                                .filter(date -> date.isAfter(startOfBaseMonth.minusDays(1)))
                                .filter(date -> date.isBefore(endOfBaseMonth.plusDays(1)))
                                .map(localDate -> new MonthlyGodSaengResponse(localDate, validGodsaeng.getStatus())))
                .collect(Collectors.toList());
    }

    private List<GodSaeng> getValidGodsaengs(LocalDate startOfBaseMonth, LocalDate endOfBaseMonth, List<GodSaeng> godSaengs) {
        return godSaengs.stream()
                .filter(godSaeng -> godSaeng.getClosedDate().isAfter(startOfBaseMonth))
                .filter(godSaeng -> godSaeng.getOpenedDate().isBefore(endOfBaseMonth))
                .collect(Collectors.toList());
    }
}
