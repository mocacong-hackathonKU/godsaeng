package godsaeng.server.service;

import godsaeng.server.domain.*;
import godsaeng.server.dto.request.GodSaengSaveRequest;
import godsaeng.server.dto.request.ProofSaveRequest;
import godsaeng.server.dto.response.GodSaengSaveResponse;
import godsaeng.server.dto.response.GodSaengsResponse;
import godsaeng.server.dto.response.MonthlyGodSaengsResponse;
import godsaeng.server.exception.badrequest.DuplicateGodSaengException;
import godsaeng.server.exception.badrequest.DuplicateProofException;
import godsaeng.server.exception.badrequest.InvalidProofMemberException;
import godsaeng.server.exception.notfound.NotFoundGodSaengException;
import godsaeng.server.repository.GodSaengRepository;
import godsaeng.server.repository.MemberRepository;
import godsaeng.server.repository.ProofRepository;
import godsaeng.server.support.AwsS3Uploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class GodSaengServiceTest {

    @Autowired
    private GodSaengRepository godSaengRepository;
    @Autowired
    private GodSaengService godSaengService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProofRepository proofRepository;

    @MockBean
    private AwsS3Uploader awsS3Uploader;

    @PersistenceContext
    private EntityManager em;

    @DisplayName("같생을 저장할 수 있다")
    @Test
    void saveGodSaeng() {
        String title = "아침 6시 반 기상 갓생 살기";
        String description = "아침 6시 반 기상 후 유의미한 일을 하고 인증해야합니다.";
        List<Week> weeks = new ArrayList<>();
        weeks.add(Week.MON);
        weeks.add(Week.TUE);
        weeks.add(Week.WED);

        GodSaengSaveRequest request = new GodSaengSaveRequest(title, description, weeks);

        String email = "rlawjddn103@naver.com";
        Member savedMember = memberRepository.save(new Member(email, Platform.KAKAO, "11111"));

        godSaengService.save(savedMember.getId(), request);
        em.clear();
        em.flush();

        List<GodSaeng> actual = godSaengRepository.findAll();


        assertEquals(1, actual.size());
    }

    @DisplayName("같생을 조회할 수 있다")
    @Test
    void findAllGodSaeng() {
        String title = "아침 6시 반 기상 갓생 살기";
        String description = "아침 6시 반 기상 후 유의미한 일을 하고 인증해야합니다.";
        List<Week> weeks = new ArrayList<>();
        weeks.add(Week.MON);

        String email = "rlawjddn103@naver.com";
        Member savedMember = memberRepository.save(new Member(email, Platform.KAKAO, "11111"));

        godSaengRepository.save(new GodSaeng(title, description, weeks, savedMember));
        godSaengRepository.save(new GodSaeng(title, description, weeks, savedMember));

        em.clear();
        em.flush();

        GodSaengsResponse allGodSaeng = godSaengService.findAllGodSaeng();


        assertEquals(2, allGodSaeng.getGodsaengs().size());
    }

    @DisplayName("같생에 참가 신청할 수 있다")
    @Test
    void attendGodSaeng() {
        String title = "아침 6시 반 기상 갓생 살기";
        String description = "아침 6시 반 기상 후 유의미한 일을 하고 인증해야합니다.";
        List<Week> weeks = new ArrayList<>();
        weeks.add(Week.MON);

        String email1 = "rlawjddn103@naver.com";
        Member savedMember1 = memberRepository.save(new Member(email1, Platform.KAKAO, "11111"));

        String email2 = "rlawjddn102@naver.com";
        Member savedMember2 = memberRepository.save(new Member(email2, Platform.KAKAO, "12121"));

        GodSaeng savedGodSaeng = godSaengRepository.save(new GodSaeng(title, description, weeks, savedMember1));

        godSaengService.attendGodSaeng(savedMember2.getId(), savedGodSaeng.getId());

        em.clear();
        em.flush();

        GodSaeng actual = godSaengRepository.findById(savedGodSaeng.getId()).orElseThrow();

        assertEquals(2, actual.getMembers().size());
    }

    @DisplayName("같은 같생에 중복 참가 신청할 수 없다")
    @Test
    void validateDuplicateAttendGodSaeng() {
        String title = "아침 6시 반 기상 갓생 살기";
        String description = "아침 6시 반 기상 후 유의미한 일을 하고 인증해야합니다.";
        List<Week> weeks = new ArrayList<>();
        weeks.add(Week.MON);

        String email1 = "rlawjddn103@naver.com";
        Member savedMember1 = memberRepository.save(new Member(email1, Platform.KAKAO, "11111"));

        String email2 = "rlawjddn102@naver.com";
        Member savedMember2 = memberRepository.save(new Member(email2, Platform.KAKAO, "12121"));

        GodSaeng savedGodSaeng = godSaengRepository.save(new GodSaeng(title, description, weeks, savedMember1));

        godSaengService.attendGodSaeng(savedMember2.getId(), savedGodSaeng.getId());

        em.clear();
        em.flush();

        assertThrows(DuplicateGodSaengException.class, () -> godSaengService.attendGodSaeng(savedMember2.getId(), savedGodSaeng.getId()));
    }

    @DisplayName("월별 같생 목록을 조회할 수 있다.")
    @Test
    void monthlyGodSaeng() {
        String title = "아침 6시 반 기상 갓생 살기";
        String description = "아침 6시 반 기상 후 유의미한 일을 하고 인증해야합니다.";
        List<Week> weeks = new ArrayList<>();
        weeks.add(Week.MON);
        weeks.add(Week.WED);
        weeks.add(Week.FRI);

        String email1 = "rlawjddn103@naver.com";
        Member savedMember1 = memberRepository.save(new Member(email1, Platform.KAKAO, "11111"));

        String email2 = "rlawjddn102@naver.com";
        memberRepository.save(new Member(email2, Platform.KAKAO, "12121"));

        godSaengRepository.save(new GodSaeng(title, description, weeks, savedMember1));

        em.clear();
        em.flush();

        MonthlyGodSaengsResponse monthlyGodSaeng = godSaengService.findMonthlyGodSaeng(1L, LocalDate.now());

        // 테스트에 대한 고민 필요
    }

    @DisplayName("같생에 인증 글을 올릴 수 있다")
    @Test
    void saveProof() throws IOException {
        List<Week> weeks = new ArrayList<>();
        weeks.add(Week.MON);
        weeks.add(Week.TUE);
        String email = "dlawotn3@naver.com";
        Member savedMember = memberRepository.save(new Member(email, Platform.KAKAO, "11111"));
        GodSaengSaveResponse savedGodSaeng = godSaengService.save(savedMember.getId(),
                new GodSaengSaveRequest("아침 6시 반 기상 갓생 살기",
                "아침 6시 반 기상 후 유의미한 일을 하고 인증해야합니다.", weeks));
        String expected = "test_img.jpg";
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/images/" + expected);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("test_img", expected, "jpg",
                fileInputStream);
        when(awsS3Uploader.uploadImage(mockMultipartFile)).thenReturn("test_img.jpg");
        ProofSaveRequest request = new ProofSaveRequest("hihih");
        godSaengService.saveProof(savedMember.getId(), savedGodSaeng.getId(), mockMultipartFile, request);

        List<Proof> actual = proofRepository.findAll();
        em.clear();
        em.flush();

        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual.get(0).getContent()).isEqualTo("hihih");
        assertThat(actual.get(0).getMember()).isEqualTo(savedMember);
        assertThat(actual.get(0).getProofImage()).isNotNull();
    }

    @DisplayName("존재하지 않는 같생에 대해 인증 글을 올릴 수 없다")
    @Test
    void saveProofNotExistsGodSaeng() throws IOException{
        String email = "dlawotn3@naver.com";
        Member savedMember = memberRepository.save(new Member(email, Platform.KAKAO, "11111"));
        String expected = "test_img.jpg";
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/images/" + expected);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("test_img", expected, "jpg",
                fileInputStream);
        when(awsS3Uploader.uploadImage(mockMultipartFile)).thenReturn("test_img.jpg");
        ProofSaveRequest request = new ProofSaveRequest("hihih");

        assertThrows(NotFoundGodSaengException.class,
                () -> godSaengService.saveProof(savedMember.getId(), 9999L, mockMultipartFile,
                        request));
    }

    @DisplayName("참가 신청을 안 한 같생에는 인증 글을 올릴 수 없다")
    @Test
    void saveProofNotAttend() throws IOException {
        List<Week> weeks = new ArrayList<>();
        weeks.add(Week.MON);
        weeks.add(Week.TUE);
        String email = "dlawotn3@naver.com";
        Member savedMember1 = memberRepository.save(new Member(email, Platform.KAKAO, "11111"));
        Member savedMember2 = memberRepository.save(new Member("dlawotn1@naver.com", Platform.KAKAO,
                "11111"));
        GodSaengSaveResponse savedGodSaeng = godSaengService.save(savedMember1.getId(),
                new GodSaengSaveRequest("아침 6시 반 기상 갓생 살기",
                        "아침 6시 반 기상 후 유의미한 일을 하고 인증해야합니다.", weeks));
        String expected = "test_img.jpg";
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/images/" + expected);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("test_img", expected, "jpg",
                fileInputStream);
        when(awsS3Uploader.uploadImage(mockMultipartFile)).thenReturn("test_img.jpg");
        ProofSaveRequest request = new ProofSaveRequest("hihih");

        assertThrows(InvalidProofMemberException.class,
                () -> godSaengService.saveProof(savedMember2.getId(), savedGodSaeng.getId(), mockMultipartFile,
                        request));
    }

    @DisplayName("회원은 같은 같생에 대해 하루에 두 번 이상 인증 글을 올릴 수 없다")
    @Test
    void saveProof2Times() throws IOException {
        List<Week> weeks = new ArrayList<>();
        weeks.add(Week.MON);
        weeks.add(Week.TUE);
        String email = "dlawotn3@naver.com";
        Member savedMember = memberRepository.save(new Member(email, Platform.KAKAO, "11111"));
        GodSaengSaveResponse savedGodSaeng = godSaengService.save(savedMember.getId(),
                new GodSaengSaveRequest("아침 6시 반 기상 갓생 살기",
                        "아침 6시 반 기상 후 유의미한 일을 하고 인증해야합니다.", weeks));
        String expected = "test_img.jpg";
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/images/" + expected);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("test_img", expected, "jpg",
                fileInputStream);
        when(awsS3Uploader.uploadImage(mockMultipartFile)).thenReturn("test_img.jpg");
        ProofSaveRequest request = new ProofSaveRequest("hihih");
        godSaengService.saveProof(savedMember.getId(), savedGodSaeng.getId(), mockMultipartFile,
                request);

        assertThrows(DuplicateProofException.class,
                () -> godSaengService.saveProof(savedMember.getId(), savedGodSaeng.getId(), mockMultipartFile,
                        request));
    }
}
