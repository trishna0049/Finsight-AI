package com.finsight.platform.service.impl;

import com.finsight.platform.domain.entity.User;
import com.finsight.platform.dto.response.AssigneeResponse;
import com.finsight.platform.repository.UserRepository;
import com.finsight.platform.service.UserDirectoryService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class UserDirectoryServiceImpl implements UserDirectoryService {

    private final UserRepository userRepository;

    public UserDirectoryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<AssigneeResponse> listAssignableUsers() {
        return userRepository.findByActiveTrueOrderByUsernameAsc().stream()
                .filter(user -> user.getRoles().stream().anyMatch(role -> {
                    String roleName = role.getName().name();
                    return roleName.equals("ROLE_PLATFORM_ADMIN") || roleName.equals("ROLE_INCIDENT_ANALYST");
                }))
                .map(this::toAssignee)
                .sorted(Comparator.comparing(AssigneeResponse::username))
                .toList();
    }

    private AssigneeResponse toAssignee(User user) {
        String role = user.getRoles().stream()
                .map(r -> r.getName().name().replace("ROLE_", ""))
                .sorted()
                .findFirst()
                .orElse("INCIDENT_ANALYST");

        return new AssigneeResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                role
        );
    }
}
