package godsaeng.server.repository;

import godsaeng.server.domain.GodSaeng;
import godsaeng.server.domain.Member;
import godsaeng.server.domain.Proof;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProofRepository extends JpaRepository<Proof, Long> {
    Optional<Proof> findTopByMemberAndGodSaengOrderByCreatedTimeDesc(Member member, GodSaeng godSaeng);

    List<Proof> deleteAllByMemberId(Long memberId);
}
