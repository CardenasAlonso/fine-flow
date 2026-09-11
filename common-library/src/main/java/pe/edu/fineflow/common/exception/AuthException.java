package pe.edu.fineflow.common.exception;

import pe.edu.fineflow.common.enums.ErrorCode;

/** Errores de autenticación y autorización. */
public class AuthException extends BusinessException {

    public AuthException(ErrorCode code) {
        super(code);
    }

    public AuthException(ErrorCode code, String detail) {
        super(code, detail);
    }

    public static AuthException invalidCredentials() {
        return new AuthException(ErrorCode.INVALID_CREDENTIALS);
    }

    public static AuthException tokenExpired() {
        return new AuthException(ErrorCode.TOKEN_EXPIRED);
    }

    public static AuthException tokenInvalid() {
        return new AuthException(ErrorCode.TOKEN_INVALID);
    }

    public static AuthException tokenRevoked() {
        return new AuthException(ErrorCode.TOKEN_REVOKED);
    }

    public static AuthException userNotFound() {
        return new AuthException(ErrorCode.USER_NOT_FOUND);
    }

    public static AuthException accountLocked() {
        return new AuthException(ErrorCode.ACCOUNT_LOCKED);
    }

    public static AuthException locked(String detail) {
        return new AuthException(ErrorCode.ACCOUNT_LOCKED, detail);
    }

    public static AuthException rateLimited() {
        return new AuthException(ErrorCode.RATE_LIMITED);
    }

    public static BusinessException conflict(String code, String detail) {
        return BusinessException.conflict(code, detail);
    }

    public static BusinessException forbidden(String code, String detail) {
        return BusinessException.forbidden(code, detail);
    }

    public static BusinessException badRequest(String code, String detail) {
        return BusinessException.badRequest(code, detail);
    }
}
