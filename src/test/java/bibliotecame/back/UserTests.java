package bibliotecame.back;

import bibliotecame.back.Auth.AuthController;
import bibliotecame.back.Auth.LoginForm;
import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookRepository;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Loan.LoanModel;
import bibliotecame.back.Sanction.SanctionController;
import bibliotecame.back.Sanction.SanctionForm;
import bibliotecame.back.Sanction.SanctionService;
import bibliotecame.back.Security.jwt.TokenProvider;
import bibliotecame.back.Tag.TagRepository;
import bibliotecame.back.Tag.TagService;
import bibliotecame.back.User.UserController;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserRepository;
import bibliotecame.back.User.UserService;
import bibliotecame.back.Verification.PasswordContainer;
import bibliotecame.back.Verification.VerificationController;
import bibliotecame.back.Verification.VerificationModel;
import bibliotecame.back.Verification.VerificationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTests {

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

    @Autowired
    private UserRepository userRepository;

    @Mock
    public BookService bookService;
    @Autowired
    private BookRepository bookRepository;

    @Mock
    private TagService tagService;
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private SanctionService sanctionService;

    @Autowired
    private SanctionController sanctionController;

    @Autowired
    private VerificationService verificationService;
    @Mock
    private VerificationController verificationController;

    Authentication authentication;
    SecurityContext securityContext;


    @BeforeAll
    void setUp(){
        tagService = new TagService(tagRepository);
        bookService = new BookService(bookRepository, tagService);
        userService = new UserService(userRepository, bookService);
        userController = new UserController(userService, verificationService);
        authController = new AuthController(tokenProvider,authProvider,userService, sanctionService);
        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);
        verificationController = new VerificationController(userService,verificationService);
    }

    private void setSecurityContext(UserModel user){
        List<GrantedAuthority> auths = new ArrayList<>();
        User securityUser = new User(user.getEmail(), user.getPassword(), auths);
        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

    }

    @Test
    void testAddUser(){
        String email = "name@mail.austral.edu.ar";
        ResponseEntity create = userController.createUser(new UserModel(email, "123abcd", "Name","LastName", "+54 (911) 1234 5678"));

        assertThat(create.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        UserModel savedUser = userService.findUserByEmail(email);

        assertThat(userController.getUserModel(savedUser.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(userController.createUser(new UserModel(email, "123qwee", "name", "lastname", "11111111")).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    // asserts failure for: empty email, no .austral.edu., no @
    void testFailureOnInvalidEmail(){
        assertThat(userController.createUser(new UserModel("name@gmail.com", "123abcABC", "Name",
                "LastName", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(userController.createUser(new UserModel("name.austral.edu.ar", "123abcABC", "Name",
                "LastName", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(userController.createUser(new UserModel("", "123abcd", "Name",
                "LastName", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    //asserts failure for: empty password, short password
    void testFailureOnInvalidPassword(){
        assertThat(userController.createUser(new UserModel("name@ing.austral.edu.ar", "123ab", "Name",
                "LastName", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(userController.createUser(new UserModel("name@ing.austral.edu.ar", "", "Name",
                "LastName", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    //asserts failure for: empty first name, empty last name
    void testFailureOnInvalidName(){
        assertThat(userController.createUser(new UserModel("name@ing.austral.edu.ar", "123abcd", "",
                "LastName", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(userController.createUser(new UserModel("name@ing.austral.edu.ar", "123abcd", "Name",
                "", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    //asserts failure for empty phone number
    void testFailureOnInvalidPhoneNumber(){
        assertThat(userController.createUser(new UserModel("name@ing.austral.edu.ar", "123abcd", "Name",
                "LastName", ""))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    //test delete user
    void testDeleteUser(){
        UserModel user = (UserModel)userController.createUser(new UserModel(RandomStringGenerator.getAlphaNumericString(7) +"@mail.austral.edu.ar", "123abcd", "name", "surname", "123456789")).getBody();
        assert user != null;
        user.setVerified(true);
        userService.saveWithoutEncryption(user);
        setSecurityContext(user);
        verificationService.deleteVerification(verificationService.findVerificationByUserModel(user));
        assertThat(userController.deleteUser(user.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        assertThat(((ErrorMessage) Objects.requireNonNull(userController.getUserModel(user.getId()).getBody())).getMessage()).isEqualTo("¡El usuario no existe!");
    }

    @Test
    void testDeleteUserWithBadRequest(){

        UserModel user = (UserModel) userController.createUser(new UserModel(RandomStringGenerator.getAlphaNumericString(7) +"@mail.austral.edu.ar", "123abcd", "name", "surname", "123456789")).getBody();
        assert user != null;
        setSecurityContext(user);

        BookModel bookModel = new BookModel(RandomStringGenerator.getAlphabeticString(7), 1999, RandomStringGenerator.getAlphabeticString(20), RandomStringGenerator.getAlphabeticString(20));

        List<CopyModel> copies = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            copies.add(new CopyModel(RandomStringGenerator.getAlphaNumericString(6)));
        }
        bookModel.setCopies(copies);
        bookService.saveBook(bookModel);

        List<LoanModel> loans = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            loans.add(new LoanModel(copies.get(i), LocalDate.now(), LocalDate.now().plus(Period.ofDays(5))));
        }
        user.setLoans(loans);
        userService.saveUser(user);

        assertThat(userController.deleteUser(user.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);


    }

    @Test
    //test failure for not logged in
    void testFailureNotLoggedOnDelete(){
        UserModel user = (UserModel)userController.createUser(new UserModel(RandomStringGenerator.getAlphaNumericString(7) +"@mail.austral.edu.ar", "123abcd", "name", "surname", "123456789")).getBody();

        assert user != null;
        assertThat(userController.deleteUser(user.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    //test failure for deleting some other account
    void testFailureWrongAccountOnDelete(){
        UserModel user = (UserModel)userController.createUser(new UserModel(RandomStringGenerator.getAlphaNumericString(7) +"@mail.austral.edu.ar", "123abcd", "name", "surname", "123456789")).getBody();
        UserModel user2 = (UserModel)userController.createUser(new UserModel(RandomStringGenerator.getAlphaNumericString(7) +"@mail.austral.edu.ar", "123abcd", "name", "surname", "123456789")).getBody();

        assert user != null;
        setSecurityContext(user);

        assert user2 != null;
        assertThat(userController.deleteUser(user2.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testUnauthorizedOnTryingToModifyAnIdDifferentFromMyId(){
        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(7) +"@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);

        setSecurityContext(user);

        assertThat(userController.updateUser(-1,user).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testUnauthorizedWhenUpdatingUserWithoutBeingLoggedIn(){
        UserModel user = new UserModel( RandomStringGenerator.getAlphaNumericString(7) +"@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        assertThat(userController.updateUser(user.getId(),user).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testUnauthorizedWhenUsingUpdateForChangingEmail(){
        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(7) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);

        setSecurityContext(user);

        user.setEmail("notKhalilTest@ing.austral.edu.ar");
        assertThat(userController.updateUser(user.getId(),user).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testUnauthorizedWhenUpdatingBodyUserIdDiffersFromLoggedId(){
        String name = RandomStringGenerator.getAlphabeticString(10);
        UserModel user = new UserModel(name + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        UserModel user2 = new UserModel(RandomStringGenerator.getAlphabeticString(10) +"@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");

        setSecurityContext(user);

        assertThat(userController.updateUser(user.getId(),user2).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testUpdateUserWthBadRequest(){
        String name = RandomStringGenerator.getAlphabeticString(10);
        UserModel user = new UserModel(name + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userService.saveUser(user);
        UserModel user4 = new UserModel(name +"@ing.austral.edu.ar","test123","","Stessens","1151111111");
        UserModel user5 = new UserModel(name +"@ing.austral.edu.ar","test123","Khalil","","1151111111");
        UserModel user6 = new UserModel(name +"@ing.austral.edu.ar","test123","Khalil","Stessens","");

        setSecurityContext(user);

        assertThat(userController.updateUser(user.getId(),user4).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(userController.updateUser(user.getId(),user5).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(userController.updateUser(user.getId(),user6).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testOkWhenSuccessfullyUpdatingAUserAndVerifyUpdatedPassword(){
        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(6) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);

        setSecurityContext(user);

        user.setPassword("estaesmassegura1432");
        assertThat(userController.updateUser(user.getId(),user).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }

    @Test
    void testAuthReturnsAToken(){
        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(6) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        user.setVerified(true);
        userService.saveWithoutEncryption(user);
        LoginForm loginForm = new LoginForm(user.getEmail(), "test123");
        assertThat(authController.authenticate(loginForm).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }

    @Test
    void testAuthReturnsUNAUTHORIZED(){

        UserModel admin = new UserModel("admin" + RandomStringGenerator.getAlphabeticString(6)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        admin.setAdmin(true);
        userService.saveUser(admin);
        setSecurityContext(admin);

        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(6) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);

        assertThat(sanctionController.createSanction(new SanctionForm(user.getEmail(), "reason", LocalDate.now().plus(Period.ofDays(20)))).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);


        LoginForm loginForm = new LoginForm(user.getEmail(), "test123");
        assertThat(authController.authenticate(loginForm).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    public void testUnverifiedUserCantLogin(){
        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(16) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        LoginForm loginForm = new LoginForm(user.getEmail(),user.getPassword());
        assertThat(((ErrorMessage) Objects.requireNonNull(authController.authenticate(loginForm).getBody())).getMessage()).isEqualTo("¡Por favor verifique su dirección de correo para poder acceder a Bibliotecame!");
    }
    @Test
    public void testUserCanVerify(){
        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(16) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        String token = verificationService.findVerificationByUserModel(user).getToken();
        assertThat(userService.findUserById(user.getId()).isVerified()).isEqualTo(false);
        assertThat(verificationController.verifyAccount(token).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userService.findUserById(user.getId()).isVerified()).isEqualTo(true);
    }

    @Test
    public void testUserCanResetPassword(){
        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(16) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        String token = verificationService.findVerificationByUserModel(user).getToken();
        assertThat(verificationController.verifyAccount(token).getStatusCode()).isEqualTo(HttpStatus.OK);
        userController.forgotPassword(user.getEmail());
        token = verificationService.findVerificationByUserModel(user).getToken();
        PasswordContainer passwordContainer = new PasswordContainer("newpassword123");
        verificationController.resetPassword(token,passwordContainer);
        LoginForm loginForm = new LoginForm(user.getEmail(),"newpassword123");
        assertThat(authController.authenticate(loginForm).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }

    @Test
    void testForgotPasswordBAD_REQUEST(){
        assertThat(userController.forgotPassword("asd").getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);

        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(16) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userService.saveUser(user);
        setSecurityContext(user);

        assertThat(userController.forgotPassword(user.getEmail()).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testForgotPasswordUNAUTHORIZED(){
        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(16) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        assertThat(userController.forgotPassword(user.getEmail()).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testUpdatePassword(){

        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(6) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);

        setSecurityContext(user);

        PasswordContainer passwordContainer = new PasswordContainer();
        passwordContainer.setPassword("newPasswordte123");
        assertThat(userController.updatePassword(user.getId(),passwordContainer).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

    }

    @Test
    void testUpdatePasswordUNAUTHORIZED(){

        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(6) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");

        setSecurityContext(user);

        PasswordContainer passwordContainer = new PasswordContainer();
        passwordContainer.setPassword("newPasswordte123");
        assertThat(userController.updatePassword(user.getId(),passwordContainer).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

        UserModel user1 = new UserModel(RandomStringGenerator.getAlphaNumericString(6) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user1);
        setSecurityContext(user1);

        PasswordContainer passwordContainer1 = new PasswordContainer();
        passwordContainer1.setPassword("newPasswordte123");
        assertThat(userController.updatePassword(0,passwordContainer1).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    void testUpdatePasswordWithInvalidPassword(){

        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(6) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);

        setSecurityContext(user);

        PasswordContainer passwordContainer = new PasswordContainer();
        passwordContainer.setPassword("te123");
        assertThat(userController.updatePassword(user.getId(),passwordContainer).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    void testGetUsersOK(){

        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(6) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        user.setAdmin(true);
        userService.saveUser(user);

        setSecurityContext(user);

        assertThat(userController.getUsers("").getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(userController.getUsers("i").getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }

    @Test
    void testGetUsersUNAUTHORIZED(){

        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(6) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);

        setSecurityContext(user);

        assertThat(userController.getUsers("").getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testGetLogged(){
        setSecurityContext(new UserModel(RandomStringGenerator.getAlphaNumericString(6) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111"));
        assertThat(userController.getLoggedUser().getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);

        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(6) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);

        setSecurityContext(user);
        assertThat(userController.getLoggedUser().getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

    }

    @Test
    void testExceptionOnWrongId(){
        assertThrows(RuntimeException.class, () -> userService.findUserById(-1));
    }

    @Test
    void testVerificationInfo(){
        VerificationModel verificationInfo = new VerificationModel();
        verificationInfo.setToken("123qweasd456rtyfgh789uiojkl");
        verificationInfo.setUserModel(new UserModel("aa@ing.austral.edu.ar","password1", "name", "surname", "phone"));

        assertThat(verificationInfo.getToken()).isEqualTo("123qweasd456rtyfgh789uiojkl");
        assertThat(verificationInfo.getUserModel().getEmail()).isEqualTo("aa@ing.austral.edu.ar");
    }

    @Test
    void testBadRequestVerifyAccount(){
        assertThat(verificationController.verifyAccount("asd").getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testBadRequestResetPassword(){
        assertThat(verificationController.resetPassword("asd", new PasswordContainer("123qweasd")).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);

        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(16) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        String token = verificationService.findVerificationByUserModel(user).getToken();
        assertThat(verificationController.verifyAccount(token).getStatusCode()).isEqualTo(HttpStatus.OK);
        userController.forgotPassword(user.getEmail());
        token = verificationService.findVerificationByUserModel(user).getToken();
        PasswordContainer passwordContainer = new PasswordContainer("newpassword");
        assertThat(verificationController.resetPassword(token,passwordContainer).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);

    }


}
