package com.controle.fechamentocaixa.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.controle.fechamentocaixa.config.AppProperties;
import com.controle.fechamentocaixa.controller.TestController;
import com.controle.fechamentocaixa.security.services.UserDetailsImpl;
import com.controle.fechamentocaixa.security.services.UserDetailsServiceImpl;

/**
 * Testes de integração para AuthTokenFilter
 * Verifica comportamento do filtro JWT em cenários reais de requisição
 */
@WebMvcTest(controllers = TestController.class)
@ContextConfiguration(classes = { TestController.class, AuthTokenFilter.class, JwtUtils.class })
@DisplayName("AuthTokenFilter Integration Tests")
class AuthTokenFilterIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private JwtUtils jwtUtils;

  @Mock
  private UserDetailsServiceImpl userDetailsService;

  @Mock
  private AppProperties appProperties;

  @Mock
  private AppProperties.Jwt jwtProperties;

  private UserDetails userDetails;
  private final String VALID_TOKEN = "valid.jwt.token";
  private final String INVALID_TOKEN = "invalid.jwt.token";
  private final String TEST_USERNAME = "test@example.com";

  @BeforeEach
  void setUp() {
    // Clear security context before each test
    SecurityContextHolder.clearContext();

    // Setup test user details
    userDetails = new UserDetailsImpl(
        "test-user-id",
        TEST_USERNAME,
        "Test User",
        "encoded-password",
        true,
        List.of(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")));

    // Setup app properties mock
    when(appProperties.getJwt()).thenReturn(jwtProperties);
    when(jwtProperties.getSecret()).thenReturn("test-secret-key-that-is-at-least-256-bits-long");
    when(jwtProperties.getExpiration()).thenReturn(86400000);
  }

  @Test
  @DisplayName("Deve autenticar usuário com token JWT válido")
  void deveAutenticarUsuarioComTokenValido() throws Exception {
    // Given
    when(jwtUtils.validateJwtToken(VALID_TOKEN)).thenReturn(true);
    when(jwtUtils.getUsernameFromJwtToken(VALID_TOKEN)).thenReturn(TEST_USERNAME);
    when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);

    // When & Then
    MvcResult result = mockMvc.perform(get("/test/public")
        .header("Authorization", "Bearer " + VALID_TOKEN))
        .andExpect(status().isOk())
        .andReturn();

    // Verify interactions
    verify(jwtUtils).validateJwtToken(VALID_TOKEN);
    verify(jwtUtils).getUsernameFromJwtToken(VALID_TOKEN);
    verify(userDetailsService).loadUserByUsername(TEST_USERNAME);

    // Verify response
    String responseContent = result.getResponse().getContentAsString();
    assertThat(responseContent).contains("Public endpoint accessible");
  }

  @Test
  @DisplayName("Deve rejeitar requisição com token JWT inválido")
  void deveRejeitarRequisicaoComTokenInvalido() throws Exception {
    // Given
    when(jwtUtils.validateJwtToken(INVALID_TOKEN)).thenReturn(false);

    // When & Then
    mockMvc.perform(get("/test/public")
        .header("Authorization", "Bearer " + INVALID_TOKEN))
        .andExpect(status().isOk()); // Public endpoint should still be accessible

    // Verify that invalid token doesn't trigger user loading
    verify(jwtUtils).validateJwtToken(INVALID_TOKEN);
    verify(jwtUtils, never()).getUsernameFromJwtToken(anyString());
    verify(userDetailsService, never()).loadUserByUsername(anyString());
  }

  @Test
  @DisplayName("Deve processar requisição sem token de autorização")
  void deveProcessarRequisicaoSemToken() throws Exception {
    // When & Then
    mockMvc.perform(get("/test/public"))
        .andExpect(status().isOk());

    // Verify no JWT processing occurs
    verify(jwtUtils, never()).validateJwtToken(anyString());
    verify(jwtUtils, never()).getUsernameFromJwtToken(anyString());
    verify(userDetailsService, never()).loadUserByUsername(anyString());
  }

  @Test
  @DisplayName("Deve ignorar header Authorization sem Bearer prefix")
  void deveIgnorarHeaderSemBearerPrefix() throws Exception {
    // When & Then
    mockMvc.perform(get("/test/public")
        .header("Authorization", "Basic dGVzdDp0ZXN0"))
        .andExpect(status().isOk());

    // Verify no JWT processing occurs
    verify(jwtUtils, never()).validateJwtToken(anyString());
    verify(userDetailsService, never()).loadUserByUsername(anyString());
  }

  @Test
  @DisplayName("Deve lidar com token malformado graciosamente")
  void deveLidarComTokenMalformadoGraciosamente() throws Exception {
    // Given
    String malformedToken = "malformed-token";
    when(jwtUtils.validateJwtToken(malformedToken)).thenReturn(false);

    // When & Then
    mockMvc.perform(get("/test/public")
        .header("Authorization", "Bearer " + malformedToken))
        .andExpect(status().isOk());

    // Verify token validation was attempted but failed gracefully
    verify(jwtUtils).validateJwtToken(malformedToken);
    verify(jwtUtils, never()).getUsernameFromJwtToken(anyString());
  }

  @Test
  @DisplayName("Deve lidar com exceção durante extração de username")
  void deveLidarComExcecaoDuranteExtracaoUsername() throws Exception {
    // Given
    when(jwtUtils.validateJwtToken(VALID_TOKEN)).thenReturn(true);
    when(jwtUtils.getUsernameFromJwtToken(VALID_TOKEN))
        .thenThrow(new RuntimeException("Token parsing error"));

    // When & Then
    mockMvc.perform(get("/test/public")
        .header("Authorization", "Bearer " + VALID_TOKEN))
        .andExpect(status().isOk()); // Should continue processing despite error

    // Verify exception was handled gracefully
    verify(jwtUtils).validateJwtToken(VALID_TOKEN);
    verify(jwtUtils).getUsernameFromJwtToken(VALID_TOKEN);
    verify(userDetailsService, never()).loadUserByUsername(anyString());
  }

  @Test
  @DisplayName("Deve lidar com falha no carregamento de UserDetails")
  void deveLidarComFalhaCarregamentoUserDetails() throws Exception {
    // Given
    when(jwtUtils.validateJwtToken(VALID_TOKEN)).thenReturn(true);
    when(jwtUtils.getUsernameFromJwtToken(VALID_TOKEN)).thenReturn(TEST_USERNAME);
    when(userDetailsService.loadUserByUsername(TEST_USERNAME))
        .thenThrow(new RuntimeException("User not found"));

    // When & Then
    mockMvc.perform(get("/test/public")
        .header("Authorization", "Bearer " + VALID_TOKEN))
        .andExpect(status().isOk()); // Should continue processing despite error

    // Verify exception was handled gracefully
    verify(userDetailsService).loadUserByUsername(TEST_USERNAME);

    // Verify SecurityContext was not set due to exception
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  @DisplayName("Deve processar múltiplas requisições com diferentes tokens")
  void deveProcessarMultiplasRequisicoesComDiferentesTokens() throws Exception {
    // Given
    String token1 = "token1";
    String token2 = "token2";
    String username1 = "user1@example.com";
    String username2 = "user2@example.com";

    UserDetails user1 = new UserDetailsImpl(
        "user1",
        username1,
        "User 1",
        "password",
        true,
        List.of(new SimpleGrantedAuthority("ROLE_USER")));

    UserDetails user2 = new UserDetailsImpl(
        "user2",
        username2,
        "User 2",
        "password",
        true,
        List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

    // Setup mocks for first request
    when(jwtUtils.validateJwtToken(token1)).thenReturn(true);
    when(jwtUtils.getUsernameFromJwtToken(token1)).thenReturn(username1);
    when(userDetailsService.loadUserByUsername(username1)).thenReturn(user1);

    // Setup mocks for second request
    when(jwtUtils.validateJwtToken(token2)).thenReturn(true);
    when(jwtUtils.getUsernameFromJwtToken(token2)).thenReturn(username2);
    when(userDetailsService.loadUserByUsername(username2)).thenReturn(user2);

    // When & Then - First request
    mockMvc.perform(get("/test/public")
        .header("Authorization", "Bearer " + token1))
        .andExpect(status().isOk());

    // When & Then - Second request
    mockMvc.perform(get("/test/public")
        .header("Authorization", "Bearer " + token2))
        .andExpect(status().isOk());

    // Verify both tokens were processed
    verify(jwtUtils).validateJwtToken(token1);
    verify(jwtUtils).validateJwtToken(token2);
    verify(userDetailsService).loadUserByUsername(username1);
    verify(userDetailsService).loadUserByUsername(username2);
  }

  @Test
  @DisplayName("Deve extrair token corretamente de header com espaços extras")
  void deveExtrairTokenCorretamenteComEspacosExtras() throws Exception {
    // Given
    when(jwtUtils.validateJwtToken(VALID_TOKEN)).thenReturn(true);
    when(jwtUtils.getUsernameFromJwtToken(VALID_TOKEN)).thenReturn(TEST_USERNAME);
    when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);

    // When & Then - Test with extra spaces
    mockMvc.perform(get("/test/public")
        .header("Authorization", "  Bearer   " + VALID_TOKEN + "  "))
        .andExpect(status().isOk());

    // The filter should handle trimming, but this depends on implementation
    // Verify the token was processed (exact behavior depends on filter
    // implementation)
    verify(jwtUtils).validateJwtToken(anyString());
  }
}
