package godsaeng.server.repository;

import godsaeng.server.domain.GodSaeng;
import godsaeng.server.domain.Member;
import godsaeng.server.domain.Proof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProofRepository extends JpaRepository<Proof, Long> {
    Optional<Proof> findTopByMemberAndGodSaengOrderByCreatedTimeDesc(Member member, GodSaeng godSaeng);

    void deleteAllByMemberId(Long memberId);

    @Query("select p from Proof p join fetch p.member m where p.godSaeng.id = :godSaengId")
    List<Proof> findProofsWithMemberByGodSaengId(Long godSaengId);
}
