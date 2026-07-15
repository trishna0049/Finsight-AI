package com.finsight.platform.repository;

import com.finsight.platform.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByIncidentIdOrderByCreatedAtAsc(Long incidentId);
}
