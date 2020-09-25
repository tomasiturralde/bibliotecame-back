package bibliotecame.back;

import bibliotecame.back.Auth.AuthController;
import bibliotecame.back.Auth.LoginForm;
import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookRepository;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Loan.LoanModel;
import bibliotecame.back.Security.jwt.TokenProvider;
import bibliotecame.back.Tag.TagRepository;
import bibliotecame.back.Tag.TagService;
import bibliotecame.back.User.UserController;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserRepository;
import bibliotecame.back.User.UserService;
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
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

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

    Authentication authentication;
    SecurityContext securityContext;


    @BeforeAll
    void setUp(){
        tagService = new TagService(tagRepository);
        bookService = new BookService(bookRepository, tagService);
        userService = new UserService(userRepository, bookService);
        userController = new UserController(userService);
        authController = new AuthController(tokenProvider,authProvider,userService);
        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);
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
        ResponseEntity<UserModel> create = userController.createUser(new UserModel(email, "123abcd", "Name","LastName", "+54 (911) 1234 5678"));

        assertThat(create.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        UserModel savedUser = userService.findUserByEmail(email);

        assertThat(userController.getUserModel(savedUser.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
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
        assertThat(userController.createUser(new UserModel("", "123abc", "Name",
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
        assertThat(userController.createUser(new UserModel("name@ing.austral.edu.ar", "123abc", "",
                "LastName", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(userController.createUser(new UserModel("name@ing.austral.edu.ar", "123abc", "Name",
                "", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    //asserts failure for empty phone number
    void testFailureOnInvalidPhoneNumber(){
        assertThat(userController.createUser(new UserModel("name@ing.austral.edu.ar", "123abc", "Name",
                "LastName", ""))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    //test delete user
    void testDeleteUser(){
        UserModel user = userController.createUser(new UserModel(RandomStringGenerator.getAlphaNumericString(7) +"@mail.austral.edu.ar", "123abc", "name", "surname", "123456789")).getBody();

        assert user != null;
        setSecurityContext(user);

        assertThat(userController.deleteUser(user.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        assertThrows(RuntimeException.class, () -> userController.getUserModel(user.getId()));
    }

    @Test
    void testDeleteUserWithBadRequest(){

        UserModel user = userController.createUser(new UserModel(RandomStringGenerator.getAlphaNumericString(7) +"@mail.austral.edu.ar", "123abc", "name", "surname", "123456789")).getBody();

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
        UserModel user = userController.createUser(new UserModel(RandomStringGenerator.getAlphaNumericString(7) +"@mail.austral.edu.ar", "123abc", "name", "surname", "123456789")).getBody();

        assertThat(userController.deleteUser(user.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    //test failure for deleting some other account
    void testFailureWrongAccountOnDelete(){
        UserModel user = userController.createUser(new UserModel(RandomStringGenerator.getAlphaNumericString(7) +"@mail.austral.edu.ar", "123abc", "name", "surname", "123456789")).getBody();
        UserModel user2 = userController.createUser(new UserModel(RandomStringGenerator.getAlphaNumericString(7) +"@mail.austral.edu.ar", "123abc", "name", "surname", "123456789")).getBody();

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
    void testBadRequestWhenTryingToUpdateUserWithInvalidPassword(){
        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(6) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);

        setSecurityContext(user);

        user.setPassword("te12");
        assertThat(userController.updateUser(user.getId(),user).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testOkWhenSuccessfullyUpdatingAUserAndVerifyUpdatedPassword(){
        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(6) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);

        setSecurityContext(user);

        user.setPassword("estaesmassegura1432");
        assertThat(userController.getUserModel(user.getId()).getBody().getPassword()).isEqualTo("test123");
        assertThat(userController.updateUser(user.getId(),user).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(userController.getUserModel(user.getId()).getBody().getPassword()).isEqualTo("estaesmassegura1432");
    }

    @Test
    void testAuthReturnsAToken(){
        UserModel user = new UserModel(RandomStringGenerator.getAlphaNumericString(6) + "@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        LoginForm loginForm = new LoginForm(user.getEmail(), "test123");
        assertThat(authController.authenticate(loginForm).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }
}
