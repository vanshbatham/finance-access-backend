package com.zorvyn.finance.dtos;

import com.zorvyn.finance.entities.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private Role role;
    private boolean isActive;
    private LocalDateTime createdAt;
}