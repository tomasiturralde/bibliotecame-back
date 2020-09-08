package bibliotecame.back;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTests {

    @Mock
    public UserController userController;

    @Mock
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    Authentication authentication;
    SecurityContext securityContext;


    @BeforeAll
    void setUp(){
        userService = new UserService(userRepository);
        userController = new UserController(userService);

        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);
    }

    @Test
    void testAddUser(){
        String email = "name@mail.austral.edu.ar";
        ResponseEntity<UserModel> create = userController.createUser(new UserModel(email, "123abc", "Name","LastName", "+54 (911) 1234 5678"));

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
        UserModel user = userController.createUser(new UserModel("mail@mail.austral.edu.ar", "123abc", "name", "surname", "123456789")).getBody();

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assert user != null;
        assertThat(userController.deleteUser(user.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        assertThrows(RuntimeException.class, () -> userController.getUserModel(user.getId()));
    }

    @Test
    //test failure for non valid id
    void testFailureNonValidIdOnDelete(){
        UserModel user = userController.createUser(new UserModel("mailmailmail@mail.austral.edu.ar", "123abc", "name", "surname", "123456789")).getBody();

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThat(userController.deleteUser(12345).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    //test failure for deleting some other account
    void testFailureWrongAccountOnDelete(){
        UserModel user = userController.createUser(new UserModel("mailmail@mail.austral.edu.ar", "123abc", "name", "surname", "123456789")).getBody();
        UserModel user2 = userController.createUser(new UserModel("mailito@mail.austral.edu.ar", "123abc", "name", "surname", "123456789")).getBody();

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assert user2 != null;
        assertThat(userController.deleteUser(user2.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testUnauthorizedOnTryingToModifyAnIdDifferentFromMyId(){
        UserModel user = new UserModel("khalilTest@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        assertThat(userController.updateUser(-1,user).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testUnauthorizedWhenUpdatingUserWithoutBeingLoggedIn(){
        UserModel user = new UserModel("khalilTest@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        assertThat(userController.updateUser(user.getId(),user).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testUnauthorizedWhenUsingUpdateForChangingEmail(){
        UserModel user = new UserModel("khalilTest@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        Mockito.when(authentication.getPrincipal()).thenReturn(userService.findUserById(user.getId()));
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        user.setEmail("notKhalilTest@ing.austral.edu.ar");
        assertThat(userController.updateUser(user.getId(),user).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testUnauthorizedWhenUpdatingBodyUserIdDiffersFromLoggedId(){
        UserModel user = new UserModel("khalilTest@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        UserModel user2 = new UserModel("khalilTest@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        Mockito.when(authentication.getPrincipal()).thenReturn(userService.findUserById(user.getId()));
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        assertThat(userController.updateUser(user.getId(),user2).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testBadRequestWhenTryingToUpdateUserWithInvalidPassword(){
        UserModel user = new UserModel("khalilTest@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        Mockito.when(authentication.getPrincipal()).thenReturn(userService.findUserById(user.getId()));
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        user.setPassword("te12");
        assertThat(userController.updateUser(user.getId(),user).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testOkWhenSuccessfullyUpdatingAUserAndVerifyUpdatedPassword(){
        UserModel user = new UserModel("khalilTest@ing.austral.edu.ar","test123","Khalil","Stessens","1151111111");
        userController.createUser(user);
        Mockito.when(authentication.getPrincipal()).thenReturn(userService.findUserById(user.getId()));
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        user.setPassword("estaesmassegura1432");
        assertThat(userController.getUserModel(user.getId()).getBody().getPassword()).isEqualTo("test123");
        assertThat(userController.updateUser(user.getId(),user).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(userController.getUserModel(user.getId()).getBody().getPassword()).isEqualTo("estaesmassegura1432");
    }

}
