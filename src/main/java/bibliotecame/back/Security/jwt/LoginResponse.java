package bibliotecame.back.Security.jwt;

@SuppressWarnings("unused")
public class LoginResponse {
    private JWTToken accessToken;
    private boolean isAdmin;
    private String fullName;

    public LoginResponse(JWTToken accessToken, boolean isAdmin, String fullName) {
        this.accessToken = accessToken;
        this.isAdmin = isAdmin;
        this.fullName = fullName;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}

