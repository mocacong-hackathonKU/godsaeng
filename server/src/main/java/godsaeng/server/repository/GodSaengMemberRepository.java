package godsaeng.server.repository;

import godsaeng.server.domain.GodSaengMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GodSaengMemberRepository extends JpaRepository<GodSaengMember, Long> {
}
