package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    // Base64-encoded key: jjwt's signWith(SignatureAlgorithm, String) decodes this as
    // base64, so the encoded text must decode to >= 512 bits (64 bytes) for HS512.
    private static final String JWT_SECRET = "MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODk=";

    private final JwtUtils jwtUtils = new JwtUtils();

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000);
    }

    @Test
    void generateJwtToken_shouldReturnNonBlankToken() {
        // given
        UserDetailsImpl userDetails = UserDetailsImpl.builder().id(1L).username("margot@teacher.com").build();
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // when
        String token = jwtUtils.generateJwtToken(authentication);

        // then
        assertThat(token).isNotBlank();
    }

    @Test
    void getUserNameFromJwtToken_shouldReturnUsername_whenTokenIsValid() {
        // given
        UserDetailsImpl userDetails = UserDetailsImpl.builder().id(1L).username("margot@teacher.com").build();
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // when
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // then
        assertThat(username).isEqualTo("margot@teacher.com");
    }

    @Test
    void validateJwtToken_shouldReturnTrue_whenTokenIsValid() {
        // given
        UserDetailsImpl userDetails = UserDetailsImpl.builder().id(1L).username("margot@teacher.com").build();
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // when
        boolean result = jwtUtils.validateJwtToken(token);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenTokenIsMalformed() {
        // when
        boolean result = jwtUtils.validateJwtToken("not-a-valid-jwt-token");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenTokenIsExpired() {
        // given
        String expiredToken = Jwts.builder()
                .setSubject("margot@teacher.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();

        // when
        boolean result = jwtUtils.validateJwtToken(expiredToken);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenSignatureIsInvalid() {
        // given
        String tokenSignedWithAnotherSecret = Jwts.builder()
                .setSubject("margot@teacher.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, "OTg3NjU0MzIxMDk4NzY1NDMyMTA5ODc2NTQzMjEwOTg3NjU0MzIxMDk4NzY1NDMyMTA5ODc2NTQzMjEwOTg3NjU0MzIxMDk4NzY1NDMyMTA=")
                .compact();

        // when
        boolean result = jwtUtils.validateJwtToken(tokenSignedWithAnotherSecret);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenTokenIsEmpty() {
        // when
        boolean result = jwtUtils.validateJwtToken("");

        // then
        assertThat(result).isFalse();
    }
}
