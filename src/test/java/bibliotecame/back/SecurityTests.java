package bibliotecame.back;


import bibliotecame.back.Auth.AuthController;
import bibliotecame.back.Auth.LoginForm;
import bibliotecame.back.Security.jwt.JWTToken;
import bibliotecame.back.Security.jwt.LoginResponse;
import bibliotecame.back.Security.jwt.TokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityTests {

    @Autowired
    AuthController authController;

    @Test
    public void testJWTTToken(){
        JWTToken token = new JWTToken("thisisatokentest");
        token.setIdToken("thisisanewtokentest");
        assertThat(token.getToken()).contains("new");
    }

    @Test
    public void testLoginResponse(){
        JWTToken token = new JWTToken("thisisanothertesttoken");
        LoginResponse login = new LoginResponse(token,true,"Administrator");
        JWTToken token2 = new JWTToken("trustMeImDifferent");
        login.setAccessToken(token2);
        assertThat(login.getAccessToken()).isNotEqualTo(token);
        assertTrue(login.isAdmin());
        if(login.isAdmin()) login.setFullName("Pepe Administrador");
        assertThat(login.getFullName()).contains("epe");
        login.setAdmin(false);
        assertFalse(login.isAdmin());
    }

    @Test
    public void testTokenProvider(){
        ResponseEntity response = authController.authenticate(new LoginForm("admin@ing.austral.edu.ar","admin123"));
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        TokenProvider provider = new TokenProvider();
        provider.init();
        Authentication auth = provider.getAuthentication(loginResponse.getAccessToken().getToken());
        auth.getPrincipal();
        assertThat(auth.getAuthorities().size()).isGreaterThan(0);
    }
}
