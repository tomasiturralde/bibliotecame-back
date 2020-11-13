package bibliotecame.back;

import bibliotecame.back.Auth.AuthController;
import bibliotecame.back.Auth.AuthService;
import bibliotecame.back.Auth.LoginForm;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Sanction.SanctionModel;
import bibliotecame.back.Sanction.SanctionService;
import bibliotecame.back.Security.jwt.TokenProvider;
import bibliotecame.back.User.UserController;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserRepository;
import bibliotecame.back.User.UserService;
import bibliotecame.back.Verification.VerificationController;
import bibliotecame.back.Verification.VerificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.time.LocalDate;
import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthTests {

    @Mock
    public UserController userController;
    @Mock
    private UserService userService;

    @Mock
    private AuthController authController;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private AuthenticationProvider authProvider;
    @Mock
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationService verificationService;
    @Mock
    private VerificationController verificationController;

    @Autowired
    private SanctionService sanctionService;
    @Mock
    public BookService bookService;

    Authentication authentication;
    SecurityContext securityContext;
    UserModel user;

    @BeforeAll
    void setUp(){
        userService = new UserService(userRepository, bookService);
        userController = new UserController(userService, verificationService);
        authController = new AuthController(tokenProvider,authProvider,userService, sanctionService);
        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);
        verificationController = new VerificationController(userService,verificationService);
        authService = new AuthService(userRepository);
        user = new UserModel(RandomStringGenerator.getAlphaNumericString(16) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        String token = verificationService.findVerificationByUserModel(user).getToken();
        verificationController.verifyAccount(token).getStatusCode();
    }

    @Test
    void testAuthenticateWrongCredentials(){
        assertThat(authController.authenticate(new LoginForm(user.getEmail(),user.getPassword()+"fail")).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testAuthEmailExists(){
        assertThat(authService.checkEmail(user.getEmail())).isEqualTo(true);
    }

    @Test
    void testLoginFormMethods(){
        LoginForm loginForm = new LoginForm();
        loginForm.setEmail(user.getEmail());
        loginForm.setPassword(user.getPassword());
        Assertions.assertEquals(user.getEmail(),loginForm.getEmail());
        Assertions.assertEquals(user.getPassword(),loginForm.getPassword());
    }

    @Test
    void testAuthenticateBranches(){
        assertThat(authController.authenticate(new LoginForm("UnexistingMail","WrongPassword")).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        UserModel unverifiedUser = new UserModel("emailNoVerificable@ing.austral.edu.ar","password123","name","last","11111");
        userService.saveUser(unverifiedUser);
        assertThat(((ErrorMessage)authController.authenticate(new LoginForm(unverifiedUser.getEmail(),unverifiedUser.getPassword())).getBody()).getMessage()).contains("verifique su");
        sanctionService.saveSanction(new SanctionModel("Tuve que hacerlo", LocalDate.now(),LocalDate.now().plus(Period.ofDays(4)),user));
        assertThat(((ErrorMessage)authController.authenticate(new LoginForm(user.getEmail(),user.getPassword())).getBody()).getMessage()).contains("sancionado");
        assertThat(authController.authenticate(new LoginForm(user.getEmail(),user.getPassword()+"a")).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
