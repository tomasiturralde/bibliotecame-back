package bibliotecame.back;

import bibliotecame.back.User.UserController;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserRepository;
import bibliotecame.back.User.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTests {

    @Mock
    public UserController userController;

    @Mock
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @BeforeAll
    void setUp(){
        userService = new UserService(userRepository);
        userController = new UserController(userService);
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
}
