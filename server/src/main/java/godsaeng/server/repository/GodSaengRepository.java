package godsaeng.server.repository;

import godsaeng.server.domain.GodSaeng;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GodSaengRepository extends JpaRepository<GodSaeng, Long> {
}
