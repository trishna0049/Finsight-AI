package com.finsight.platform.repository;

import com.finsight.platform.domain.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MetricRepository extends JpaRepository<Metric, Long> {
	@Query(value = """
			select m.metric_date,
				   avg(m.metric_value) as avg_value
			from metrics m
			where m.metric_name = 'service_availability_pct'
			group by m.metric_date
			order by m.metric_date desc
			limit 14
			""", nativeQuery = true)
	List<Object[]> findServiceAvailabilityTrendLast14Days();
}
