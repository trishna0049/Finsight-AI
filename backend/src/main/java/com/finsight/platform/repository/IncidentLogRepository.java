package com.finsight.platform.repository;

import com.finsight.platform.domain.entity.IncidentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentLogRepository extends JpaRepository<IncidentLog, Long> {
    List<IncidentLog> findTop20ByIncidentIdOrderByTimestampDesc(Long incidentId);
}
