package com.juniorpdrz.citas.auth.domain.port.in;

import com.juniorpdrz.citas.auth.dto.AuthResponse;
import com.juniorpdrz.citas.auth.dto.LoginRequest;
import com.juniorpdrz.citas.auth.dto.RegisterRequest;

public interface AuthUseCase {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}