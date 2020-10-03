package bibliotecame.back.Auth;

import bibliotecame.back.Sanction.SanctionService;
import bibliotecame.back.Security.jwt.JWTConfigurer;
import bibliotecame.back.Security.jwt.JWTToken;
import bibliotecame.back.Security.jwt.LoginResponse;
import bibliotecame.back.Security.jwt.TokenProvider;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TokenProvider tokenProvider;
    private final AuthenticationProvider authenticationProvider;
    private final UserService userService;
    private final SanctionService sanctionService;

    @Autowired
    public AuthController(TokenProvider tokenProvider, AuthenticationProvider authenticationProvider, UserService userService, SanctionService sanctionService) {
        this.tokenProvider = tokenProvider;
        this.authenticationProvider = authenticationProvider;
        this.userService = userService;
        this.sanctionService = sanctionService;
    }

    @PostMapping()
    public ResponseEntity authenticate(@Valid @RequestBody LoginForm loginForm) {

        UserModel user = userService.findUserByEmail(loginForm.getEmail());

        if(sanctionService.userIsSanctioned(user)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword());

        Authentication authentication = this.authenticationProvider.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new LoginResponse(new JWTToken(jwt), user.isAdmin(), user.getFirstName() + " " + user.getLastName()), httpHeaders, HttpStatus.OK);
    }

}
