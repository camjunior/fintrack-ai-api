package com.carlos.fintrack.service;

import com.carlos.fintrack.dto.auth.AuthResponse;
import com.carlos.fintrack.dto.auth.LoginRequest;
import com.carlos.fintrack.dto.auth.RegistroRequest;
import com.carlos.fintrack.entity.Usuario;
import com.carlos.fintrack.repository.UsuarioRepository;
import com.carlos.fintrack.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .build();

        Usuario salvo = usuarioRepository.save(usuario);

        String token = jwtService.generateToken(User.builder()
                .username(salvo.getEmail())
                .password(salvo.getSenha())
                .authorities(List.of())
                .build());

        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));

        String token = jwtService.generateToken(User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(List.of())
                .build());

        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .build();
    }
}
