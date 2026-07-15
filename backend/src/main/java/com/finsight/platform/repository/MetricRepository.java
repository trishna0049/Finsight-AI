package com.finsight.platform.repository;

import com.finsight.platform.domain.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricRepository extends JpaRepository<Metric, Long> {
}
