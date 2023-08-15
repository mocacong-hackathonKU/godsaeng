package godsaeng.server.repository;

import godsaeng.server.domain.GodSaeng;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface GodSaengRepository extends JpaRepository<GodSaeng, Long> {

    @Query("select g from GodSaengMember gm " +
            "join gm.member m " +
            "join gm.godSaeng g " +
            "where m.id = :memberId and g.createdTime between :startDate and :endDate")
    List<GodSaeng> findGodSaengsByBaseTime(Long memberId, Date startDate, Date endDate);
}
