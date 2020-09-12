package bibliotecame.back.Security.jwt;

public class LoginResponse {
    private JWTToken accessToken;
    private String role;
    private boolean isAdmin;

    public LoginResponse(JWTToken accessToken,String role, boolean isAdmin) {
        this.accessToken = accessToken;
        this.role = role;
        this.isAdmin = isAdmin;
    }

    public JWTToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(JWTToken accessToken) {
        this.accessToken = accessToken;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
