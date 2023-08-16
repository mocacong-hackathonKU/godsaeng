package godsaeng.server.repository;

import godsaeng.server.domain.GodSaeng;
import godsaeng.server.domain.GodSaengMember;
import godsaeng.server.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GodSaengMemberRepository extends JpaRepository<GodSaengMember, Long> {
    boolean existsByGodSaengAndMember(GodSaeng godSaeng, Member member);

    List<GodSaengMember> findAllGodSaengMemberByMemberId(Long memberId);
}
