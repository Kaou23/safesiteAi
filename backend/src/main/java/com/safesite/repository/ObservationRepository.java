package com.safesite.repository;

import com.safesite.entity.Observation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ObservationRepository extends JpaRepository<Observation, Long> {
    List<Observation> findBySiteId(Long siteId);

    List<Observation> findByCreatedById(Long userId);
}
