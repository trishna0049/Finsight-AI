package com.finsight.platform.repository;

import com.finsight.platform.domain.entity.Role;
import com.finsight.platform.domain.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
