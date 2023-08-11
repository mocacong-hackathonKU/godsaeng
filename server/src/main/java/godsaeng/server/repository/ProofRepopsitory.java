package godsaeng.server.repository;

import godsaeng.server.domain.GodSaeng;
import godsaeng.server.domain.Member;
import godsaeng.server.domain.Proof;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProofRepopsitory extends JpaRepository<Proof, Long> {
    Optional<Proof> findTopByMemberAndGodSaengOrderByCreatedTimeDesc(Member member, GodSaeng godSaeng);
}
