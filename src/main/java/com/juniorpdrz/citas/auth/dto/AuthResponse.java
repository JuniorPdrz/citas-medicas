package com.juniorpdrz.citas.auth.dto;

import com.juniorpdrz.citas.auth.domain.Role;

public record AuthResponse(
        String token,
        String email,
        String fullName,
        Role role
) {
}