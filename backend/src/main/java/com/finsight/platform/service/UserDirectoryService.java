package com.finsight.platform.service;

import com.finsight.platform.dto.response.AssigneeResponse;

import java.util.List;

public interface UserDirectoryService {
    List<AssigneeResponse> listAssignableUsers();
}
