package pe.edu.fineflow.common.exception;

import org.junit.jupiter.api.Test;
import pe.edu.fineflow.common.enums.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;

class AuthExceptionTest {

    @Test
    void rateLimitedUsesCorrectCodeAndStatus() {
        AuthException e = AuthException.rateLimited();
        assertThat(e.getCode()).isEqualTo("AUTH_008");
        assertThat(e.getStatus()).isEqualTo(org.springframework.http.HttpStatus.TOO_MANY_REQUESTS);
    }

    @Test
    void invalidCredentialsUsesCorrectCodeAndStatus() {
        AuthException e = AuthException.invalidCredentials();
        assertThat(e.getCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS.getCode());
        assertThat(e.getStatus()).isEqualTo(ErrorCode.INVALID_CREDENTIALS.getHttpStatus());
    }

    @Test
    void tokenExpiredUsesAuth002() {
        AuthException e = AuthException.tokenExpired();
        assertThat(e.getCode()).isEqualTo("AUTH_002");
    }

    @Test
    void lockedCarriesDetail() {
        AuthException e = AuthException.locked("Cuenta bloqueada");
        assertThat(e.getCode()).isEqualTo("AUTH_006");
        assertThat(e.getMessage()).isEqualTo("Cuenta bloqueada");
    }
}