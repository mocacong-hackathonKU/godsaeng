package godsaeng.server.service;

import godsaeng.server.domain.GodSaeng;
import godsaeng.server.domain.Week;
import godsaeng.server.dto.request.GodSaengSaveRequest;
import godsaeng.server.dto.response.GodSaengSaveResponse;
import godsaeng.server.repository.GodSaengRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GodSaengServiceTest {

    @Autowired
    private GodSaengRepository godSaengRepository;
    @Autowired
    private GodSaengService godSaengService;

    @BeforeEach
    void setUp() {
        godSaengRepository.deleteAll();
    }

    @DisplayName("같생을 저장할 수 있다.")
    @Test
    void save() {
        String title = "아침 6시 반 기상 갓생 살기";
        String description = "아침 6시 반 기상 후 유의미한 일을 하고 인증해야합니다.";
        List<Week> weeks = new ArrayList<>();
        weeks.add(Week.MON);
        weeks.add(Week.TUE);
        weeks.add(Week.WED);

        GodSaengSaveRequest request = new GodSaengSaveRequest(title, description, weeks);

        godSaengService.save(request);
        List<GodSaeng> actual = godSaengRepository.findAll();

        assertEquals(1, actual.size());
    }
}