package co.com.autenticacion.jwtsigner;

import co.com.autenticacion.jwtsigner.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

class JwtUtilTest {
    private final String secret = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF"; // 64 chars
    private final JwtUtil jwtUtil = new JwtUtil(secret, "HS256");

    @Test
    void generateAndValidateToken_shouldContainSubjectAndRoles() {
        String token = jwtUtil.generateToken("user@test.com", List.of("admin", "user"));

        Claims claims = jwtUtil.validateToken(token);

        assertThat(claims.getSubject()).isEqualTo("user@test.com");
        assertThat((List<String>) claims.get("roles", List.class)).containsExactly("admin", "user");
    }
}
