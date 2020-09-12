package bibliotecame.back.Security.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JWTToken {
    private String token;

    public JWTToken(String token) {
        this.token = token;
    }

    @JsonProperty("token")
    String getToken() {
        return token;
    }

    void setIdToken(String token) {
        this.token = token;
    }
}
