package bibliotecame.back.Security.jwt;

public class LoginResponse {
    private JWTToken accessToken;
    private boolean isAdmin;

    public LoginResponse(JWTToken accessToken, boolean isAdmin) {
        this.accessToken = accessToken;
        this.isAdmin = isAdmin;
    }

    public JWTToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(JWTToken accessToken) {
        this.accessToken = accessToken;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
