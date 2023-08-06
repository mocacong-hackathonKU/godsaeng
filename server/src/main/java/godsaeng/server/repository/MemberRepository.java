package godsaeng.server.repository;

import godsaeng.server.domain.Member;
import godsaeng.server.domain.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByPlatformAndPlatformId(Platform platform, String platformId);

    Boolean existsByNickname(String nickname);
}
