package godsaeng.server.repository;

import godsaeng.server.domain.GodSaeng;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GodSaengRepository extends JpaRepository<GodSaeng, Long> {
}
