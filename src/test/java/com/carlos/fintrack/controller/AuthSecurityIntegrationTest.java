package com.carlos.fintrack.controller;

import com.carlos.fintrack.entity.Usuario;
import com.carlos.fintrack.repository.UsuarioRepository;
import com.carlos.fintrack.service.CategoriaService;
import com.carlos.fintrack.service.DashboardService;
import com.carlos.fintrack.service.LancamentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private LancamentoService lancamentoService;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void deveExecutarFluxoAuthECategoriasNaOrdemSolicitada() throws Exception {
        String email = "alice@fintrack.com";
        String senha = "123456";

        when(usuarioRepository.existsByEmail(email)).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });

        Usuario usuarioSalvo = Usuario.builder()
                .id(1L)
                .nome("Alice")
                .email(email)
                .senha(passwordEncoder.encode(senha))
                .build();

        when(usuarioRepository.findByEmail(eq(email))).thenReturn(Optional.of(usuarioSalvo));
        when(categoriaService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Alice",
                                  "email": "alice@fintrack.com",
                                  "senha": "123456"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "alice@fintrack.com",
                                  "senha": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isUnauthorized());

        String token = loginResponse.split("\"token\":\"")[1].split("\"", 2)[0];

        mockMvc.perform(get("/api/categorias")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
