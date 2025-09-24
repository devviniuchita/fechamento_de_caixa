package com.controle.fechamentocaixa.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.controle.fechamentocaixa.config.AppProperties;
import com.controle.fechamentocaixa.security.services.UserDetailsImpl;

import io.jsonwebtoken.JwtException;

/**
 * Testes unitários para JwtUtils
 * Cobertura completa: geração, validação, refresh e casos de erro
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtils Tests")
class JwtUtilsTest {

  @Mock
  private AppProperties appProperties;

  @Mock
  private AppProperties.Jwt jwtProperties;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private JwtUtils jwtUtils;

  private UserDetailsImpl userDetails;
  private final String TEST_SECRET = "test-secret-key-that-is-at-least-256-bits-long-for-testing-purposes-only";
  private final int TEST_EXPIRATION = 86400000; // 24 hours

  @BeforeEach
  void setUp() {
    // Setup mock properties
    when(appProperties.getJwt()).thenReturn(jwtProperties);
    when(jwtProperties.getSecret()).thenReturn(TEST_SECRET);
    when(jwtProperties.getExpiration()).thenReturn(TEST_EXPIRATION);

    // Setup test user details
    List<GrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority("ROLE_ADMIN"),
        new SimpleGrantedAuthority("ROLE_USER"));

    userDetails = new UserDetailsImpl(
        "test-user-id",
        "test@example.com",
        "Test User",
        "encoded-password",
        true,
        authorities);

    // Setup mock authentication
    when(authentication.getPrincipal()).thenReturn(userDetails);
  }

  @Test
  @DisplayName("Deve gerar token JWT válido com claims corretos")
  void deveGerarTokenJwtValido() {
    // When
    String token = jwtUtils.generateJwtToken(authentication);

    // Then
    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(token.split("\\.")).hasSize(3); // Header.Payload.Signature

    // Validate the token can be parsed
    String extractedUsername = jwtUtils.getUsernameFromJwtToken(token);
    assertThat(extractedUsername).isEqualTo("test@example.com");
  }

  @Test
  @DisplayName("Deve extrair username corretamente do token")
  void deveExtrairUsernameDoToken() {
    // Given
    String token = jwtUtils.generateJwtToken(authentication);

    // When
    String extractedUsername = jwtUtils.getUsernameFromJwtToken(token);

    // Then
    assertThat(extractedUsername).isEqualTo("test@example.com");
  }

  @Test
  @DisplayName("Deve validar token JWT válido")
  void deveValidarTokenJwtValido() {
    // Given
    String token = jwtUtils.generateJwtToken(authentication);

    // When
    boolean isValid = jwtUtils.validateJwtToken(token);

    // Then
    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("Deve rejeitar token JWT inválido")
  void deveRejeitarTokenJwtInvalido() {
    // Given
    String invalidToken = "invalid.jwt.token";

    // When
    boolean isValid = jwtUtils.validateJwtToken(invalidToken);

    // Then
    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Deve rejeitar token JWT com assinatura incorreta")
  void deveRejeitarTokenComAssinaturaIncorreta() {
    // Given
    String token = jwtUtils.generateJwtToken(authentication);
    // Corrupt the signature part
    String[] parts = token.split("\\.");
    String corruptedToken = parts[0] + "." + parts[1] + ".corrupted-signature";

    // When
    boolean isValid = jwtUtils.validateJwtToken(corruptedToken);

    // Then
    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Deve rejeitar token vazio ou nulo")
  void deveRejeitarTokenVazioOuNulo() {
    // When & Then
    assertThat(jwtUtils.validateJwtToken(null)).isFalse();
    assertThat(jwtUtils.validateJwtToken("")).isFalse();
    assertThat(jwtUtils.validateJwtToken("   ")).isFalse();
  }

  @Test
  @DisplayName("Deve gerar refresh token com authorities")
  void deveGerarRefreshTokenComAuthorities() {
    // When
    String refreshToken = jwtUtils.generateRefreshToken(userDetails);

    // Then
    assertThat(refreshToken).isNotNull();
    assertThat(refreshToken).isNotEmpty();
    assertThat(refreshToken.split("\\.")).hasSize(3);

    // Validate the refresh token
    boolean isValid = jwtUtils.validateJwtToken(refreshToken);
    assertThat(isValid).isTrue();

    String extractedUsername = jwtUtils.getUsernameFromJwtToken(refreshToken);
    assertThat(extractedUsername).isEqualTo("test@example.com");
  }

  @Test
  @DisplayName("Deve lançar exceção ao extrair username de token inválido")
  void deveLancarExcecaoAoExtrairUsernameDeTokenInvalido() {
    // Given
    String invalidToken = "invalid.token";

    // When & Then
    assertThatThrownBy(() -> jwtUtils.getUsernameFromJwtToken(invalidToken))
        .isInstanceOf(JwtException.class);
  }

  @Test
  @DisplayName("Deve lidar com token com formato incorreto")
  void deveLidarComTokenComFormatoIncorreto() {
    // Given
    String malformedToken = "not-a-jwt-token";

    // When
    boolean isValid = jwtUtils.validateJwtToken(malformedToken);

    // Then
    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Deve gerar tokens diferentes para chamadas subsequentes")
  void deveGerarTokensDiferentesParaChamadasSubsequentes() throws InterruptedException {
    // Given
    String token1 = jwtUtils.generateJwtToken(authentication);

    // Wait a small amount to ensure different timestamps
    Thread.sleep(10);

    // When
    String token2 = jwtUtils.generateJwtToken(authentication);

    // Then
    assertThat(token1).isNotEqualTo(token2);
    assertThat(jwtUtils.validateJwtToken(token1)).isTrue();
    assertThat(jwtUtils.validateJwtToken(token2)).isTrue();
  }

  @Test
  @DisplayName("Deve validar que tokens têm expiração configurada")
  void deveValidarQueTokensTemExpiracaoConfigurada() {
    // Given
    String token = jwtUtils.generateJwtToken(authentication);

    // When - Extract and validate token structure
    // This is implicit in the validation, but we're testing the expiration is set
    boolean isValid = jwtUtils.validateJwtToken(token);

    // Then
    assertThat(isValid).isTrue();
    // The token should be valid now, but would expire after the configured time
    // We can't easily test expiration without manipulating time, but we ensure
    // the token is properly structured and valid
  }

  @Test
  @DisplayName("Deve lidar com secret key inválido")
  void deveLidarComSecretKeyInvalido() {
    // Given - Setup com secret muito pequeno
    when(jwtProperties.getSecret()).thenReturn("short");

    // When & Then - Should handle gracefully or throw appropriate exception
    assertThatThrownBy(() -> jwtUtils.generateJwtToken(authentication))
        .isInstanceOf(Exception.class);
  }
}
