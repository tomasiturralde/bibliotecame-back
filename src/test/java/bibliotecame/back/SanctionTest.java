package bibliotecame.back;


import bibliotecame.back.Sanction.SanctionController;
import bibliotecame.back.Sanction.SanctionForm;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SanctionTest {

    @Autowired
    private SanctionController sanctionController;

    @Autowired
    private UserService userService;

    Authentication authentication;
    SecurityContext securityContext;

    @BeforeAll
    void setUp(){
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
    void createSanctionOK(){
        UserModel admin = new UserModel("admin" + RandomStringGenerator.getAlphabeticString(6)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        admin.setAdmin(true);
        userService.saveUser(admin);
        setSecurityContext(admin);

        userService.saveUser(new UserModel("mail@mail.austral.edu.ar", "password", "Mail", "Mail", "12345678"));
        assertThat(sanctionController.createSanction(new SanctionForm("mail@mail.austral.edu.ar", "reason", LocalDate.now().plus(Period.ofDays(20)))).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }

    @Test
    void creatSanctionAndUNPROCESSABLE_ENTITY(){
        UserModel admin = new UserModel("admin" + RandomStringGenerator.getAlphabeticString(6)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        admin.setAdmin(true);
        userService.saveUser(admin);
        setSecurityContext(admin);

        userService.saveUser(new UserModel("mail1@mail.austral.edu.ar", "password", "Mail1", "Mail1", "12345678"));
        sanctionController.createSanction(new SanctionForm("mail1@mail.austral.edu.ar", "reason", LocalDate.now().plus(Period.ofDays(20))));
        assertThat(sanctionController.createSanction(new SanctionForm("mail1@mail.austral.edu.ar", "some reaspn", LocalDate.now().plus(Period.ofDays(20)))).getStatusCode()).isEqualByComparingTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void createSanctionAndBAD_REQUEST(){
        UserModel admin = new UserModel("admin" + RandomStringGenerator.getAlphabeticString(6)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        admin.setAdmin(true);
        userService.saveUser(admin);
        setSecurityContext(admin);

        assertThat(sanctionController.createSanction(new SanctionForm("mail@mail.austral.edu.ar", "reason", LocalDate.now().plus(Period.ofDays(20)))).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void creatSanctionAndEXPECTATIONS_FAILED(){
        UserModel admin = new UserModel("admin" + RandomStringGenerator.getAlphabeticString(6)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        admin.setAdmin(true);
        userService.saveUser(admin);
        setSecurityContext(admin);

        userService.saveUser(new UserModel("mail2@mail.austral.edu.ar", "password", "Mail", "Mail", "12345678"));
        assertThat(sanctionController.createSanction(new SanctionForm("mail2@mail.austral.edu.ar", "reason", LocalDate.now().minus(Period.ofDays(20)))).getStatusCode()).isEqualByComparingTo(HttpStatus.EXPECTATION_FAILED);
        assertThat(sanctionController.createSanction(new SanctionForm("mail2@mail.austral.edu.ar", "reason", LocalDate.now().plus(Period.ofMonths(4)))).getStatusCode()).isEqualByComparingTo(HttpStatus.EXPECTATION_FAILED);
    }

    @Test
    public void tryingToSanctionAnotherAdminThrowsBAD_REQUEST(){
        UserModel admin = new UserModel("adminVengativo" + RandomStringGenerator.getAlphabeticString(6)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        admin.setAdmin(true);
        userService.saveUser(admin);
        UserModel admin2 = new UserModel("adminBuenaOnda" + RandomStringGenerator.getAlphabeticString(6)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        admin2.setAdmin(true);
        userService.saveUser(admin2);
        setSecurityContext(admin);
        SanctionForm sanction = new SanctionForm(admin2.getEmail(), "Porque si.", LocalDate.now().plus(Period.ofDays(10)));
        assertThat(((ErrorMessage)sanctionController.createSanction(sanction).getBody()).getMessage()).isEqualTo("¡No puede sancionar a un administrador!");

    }
}
