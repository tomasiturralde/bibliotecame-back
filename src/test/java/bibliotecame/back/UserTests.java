package bibliotecame.back;

import bibliotecame.back.controllers.UserController;
import bibliotecame.back.models.UserModel;
import bibliotecame.back.repository.UserRepository;
import bibliotecame.back.services.UserService;
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

        ResponseEntity create = userController.createUser(new UserModel("rocio@ing.austral.edu.ar", "123abc", "Rocio","Ferreiro", "+54 (911) 1234 5678"));

        assertThat(create.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        UserModel savedUser = userService.findUserByEmail("rocio@ing.austral.edu.ar");

        assertThat(userController.getUserModel(savedUser.getId()).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

    }

    @Test
    void testBadRequest(){

        assertThat(userController.createUser(new UserModel("rocio@gmail.com", "123abcABC", "Rocio",
                "Ferreiro", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(userController.createUser(new UserModel("rocioferreiro", "123abcABC", "Rocio",
                "Ferreiro", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(userController.createUser(new UserModel("rocio@ing.austral.edu.ar", "123ab", "Rocio",
                "Ferreiro", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(userController.createUser(new UserModel("rocio@ing.austral.edu.ar", "123abc", "",
                "Ferreiro", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(userController.createUser(new UserModel("rocio@ing.austral.edu.ar", "123abc", "Rocio",
                "", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(userController.createUser(new UserModel("rocio@ing.austral.edu.ar", "", "Rocio",
                "Ferreiro", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(userController.createUser(new UserModel("", "123abc", "Rocio",
                "Ferreiro", "+54 (911) 1234 5678"))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(userController.createUser(new UserModel("rocio@ing.austral.edu.ar", "123abc", "Rocio",
                "Ferreiro", ""))
                .getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);

    }
}
