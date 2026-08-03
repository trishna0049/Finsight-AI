package com.finsight.platform.repository;

import com.finsight.platform.domain.entity.Incident;
import com.finsight.platform.domain.enums.IncidentSeverity;
import com.finsight.platform.domain.enums.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.OffsetDateTime;

public interface IncidentRepository extends JpaRepository<Incident, Long>, JpaSpecificationExecutor<Incident> {
    long countByStatus(IncidentStatus status);

    long countBySeverity(IncidentSeverity severity);

    long countByStatusAndUpdatedAtBetween(IncidentStatus status, OffsetDateTime from, OffsetDateTime to);

    @Query("select avg(i.responseTimeMs) from Incident i")
    Double findAverageResponseTimeMs();

    @Query(value = "select avg(extract(epoch from (resolved_at - created_at)) / 60.0) from incidents where resolved_at is not null", nativeQuery = true)
    Double findAverageMttrMinutes();

    @Query(value = """
            select cast(i.created_at as date) as incident_date, count(*) as total
            from incidents i
            where i.created_at >= now() - interval '14 days'
            group by cast(i.created_at as date)
            order by incident_date
            """, nativeQuery = true)
    java.util.List<Object[]> findIncidentTrendLast14Days();

    @Query(value = """
            select i.severity, count(*) as total
            from incidents i
            group by i.severity
            order by total desc
            """, nativeQuery = true)
    java.util.List<Object[]> findSeverityDistribution();

    @Query(value = """
            select s.name as service_name, count(i.id) as failures
            from incidents i
            join services s on s.id = i.service_id
            group by s.name
            order by failures desc
            limit 8
            """, nativeQuery = true)
    java.util.List<Object[]> findTopFailingServices();

    @Query(value = """
            select extract(dow from il.timestamp) as day_of_week,
                   extract(hour from il.timestamp) as hour_of_day,
                   count(*) as total
            from incident_logs il
            where il.timestamp >= now() - interval '14 days'
            group by day_of_week, hour_of_day
            order by day_of_week, hour_of_day
            """, nativeQuery = true)
    java.util.List<Object[]> findIncidentHeatmapLast14Days();

    @Query(value = """
            select cast(i.created_at as date) as incident_date,
                   count(*) as breach_count
            from incidents i
            where i.created_at >= now() - interval '14 days'
              and i.response_time_ms > 1500
            group by cast(i.created_at as date)
            order by incident_date
            """, nativeQuery = true)
    java.util.List<Object[]> findSlaBreachTrendLast14Days();
}

