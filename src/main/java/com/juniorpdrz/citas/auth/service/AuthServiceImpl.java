package com.juniorpdrz.citas.auth.service;

import com.juniorpdrz.citas.auth.domain.Role;
import com.juniorpdrz.citas.auth.domain.User;
import com.juniorpdrz.citas.auth.domain.port.in.AuthUseCase;
import com.juniorpdrz.citas.auth.domain.port.out.UserRepositoryPort;
import com.juniorpdrz.citas.auth.dto.AuthResponse;
import com.juniorpdrz.citas.auth.dto.LoginRequest;
import com.juniorpdrz.citas.auth.dto.RegisterRequest;
import com.juniorpdrz.citas.auth.security.JwtService;
import com.juniorpdrz.citas.auth.security.UserPrincipal;
import com.juniorpdrz.citas.shared.exception.DuplicateResourceException;
import com.juniorpdrz.citas.shared.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepositoryPort.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Ya existe una cuenta registrada con ese email");
        }

        User newUser = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.PACIENTE)
                .phone(request.phone())
                .build();

        User savedUser = userRepositoryPort.save(newUser);

        String token = jwtService.generateToken(
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getId()
        );

        return new AuthResponse(token, savedUser.getEmail(), savedUser.getFullName(), savedUser.getRole());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Email o contrasena incorrectos");
        }

        User user = userRepositoryPort.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Email o contrasena incorrectos"));

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );

        return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole());
    }
}
