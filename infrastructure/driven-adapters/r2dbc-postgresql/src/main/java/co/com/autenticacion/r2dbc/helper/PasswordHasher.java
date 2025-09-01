package co.com.autenticacion.r2dbc.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHasher {
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordHasher() {
    }

    public static String hash(String raw) {
        return ENCODER.encode(raw);
    }
}
