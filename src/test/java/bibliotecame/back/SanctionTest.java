package bibliotecame.back;


import bibliotecame.back.Sanction.SanctionController;
import bibliotecame.back.Sanction.SanctionDisplay;
import bibliotecame.back.Sanction.SanctionForm;
import bibliotecame.back.Sanction.SanctionModel;
import bibliotecame.back.Sanction.SanctionService;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
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
    private SanctionService sanctionService;

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

    @Test
    public void testGetActiveList_OK(){
        UserModel admin = new UserModel("admin" + RandomStringGenerator.getAlphabeticString(6)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        admin.setAdmin(true);
        userService.saveUser(admin);
        setSecurityContext(admin);

        String mail = "forList" + RandomStringGenerator.getAlphabeticString(10) + "@mail.austral.edu.ar";
        userService.saveUser(new UserModel(mail, "password", "Mail", "Mail", "12345678"));
        sanctionController.createSanction(new SanctionForm(mail, "reason", LocalDate.now().plus(Period.ofDays(10))));

        mail = "forList" + RandomStringGenerator.getAlphabeticString(10) + "@mail.austral.edu.ar";
        userService.saveUser(new UserModel(mail, "password", "Mail", "Mail", "12345678"));
        sanctionController.createSanction(new SanctionForm(mail, "reason", LocalDate.now().plus(Period.ofDays(15))));

        mail = "forList" + RandomStringGenerator.getAlphabeticString(10) + "@mail.austral.edu.ar";
        userService.saveUser(new UserModel(mail, "password", "Mail", "Mail", "12345678"));
        sanctionController.createSanction(new SanctionForm(mail, "reason", LocalDate.now().plus(Period.ofDays(20))));

        mail = "forList" + RandomStringGenerator.getAlphabeticString(10) + "@mail.austral.edu.ar";
        userService.saveUser(new UserModel(mail, "password", "Mail", "Mail", "12345678"));
        sanctionController.createSanction(new SanctionForm(mail, "reason", LocalDate.now().plus(Period.ofDays(25))));

        Page<SanctionDisplay> list = (Page<SanctionDisplay>) sanctionController.getSanctionList(0, 10, "").getBody();
        assert list != null;
        assertThat(list.getTotalElements()).isGreaterThan(3);

        Page<SanctionDisplay> list2 = (Page<SanctionDisplay>) sanctionController.getSanctionList(0, 10, "forList").getBody();
        assert list2 != null;
        assertThat(list2.getTotalElements()).isEqualTo(4);

        Page<SanctionDisplay> list3 = (Page<SanctionDisplay>) sanctionController.getSanctionList(0, 10, "holacomoestasholacomoestas").getBody();
        assert list3 != null;
        assertThat(list3.getTotalElements()).isEqualTo(0);

    }


    @Test
    public void testGetActiveList_UNAUTHORIZED(){
        UserModel admin = new UserModel("admin" + RandomStringGenerator.getAlphabeticString(6)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        admin.setAdmin(true);
        userService.saveUser(admin);
        setSecurityContext(admin);

        String mail = "forUNAUTH" + RandomStringGenerator.getAlphabeticString(10) + "@mail.austral.edu.ar";
        userService.saveUser(new UserModel(mail, "password", "Mail", "Mail", "12345678"));
        sanctionController.createSanction(new SanctionForm(mail, "reason", LocalDate.now().plus(Period.ofDays(10))));

        mail = "forUNAUTH" + RandomStringGenerator.getAlphabeticString(10) + "@mail.austral.edu.ar";
        userService.saveUser(new UserModel(mail, "password", "Mail", "Mail", "12345678"));
        sanctionController.createSanction(new SanctionForm(mail, "reason", LocalDate.now().plus(Period.ofDays(15))));

        mail = "forUNAUTH" + RandomStringGenerator.getAlphabeticString(10) + "@mail.austral.edu.ar";
        userService.saveUser(new UserModel(mail, "password", "Mail", "Mail", "12345678"));
        sanctionController.createSanction(new SanctionForm(mail, "reason", LocalDate.now().plus(Period.ofDays(20))));

        mail = "forUNAUTH" + RandomStringGenerator.getAlphabeticString(10) + "@mail.austral.edu.ar";
        userService.saveUser(new UserModel(mail, "password", "Mail", "Mail", "12345678"));
        sanctionController.createSanction(new SanctionForm(mail, "reason", LocalDate.now().plus(Period.ofDays(25))));


        UserModel NOTadmin = new UserModel("NOTadmin" + RandomStringGenerator.getAlphabeticString(6)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        NOTadmin.setAdmin(false);
        userService.saveUser(NOTadmin);
        setSecurityContext(NOTadmin);

        assertThat(sanctionController.getSanctionList(0, 10, "").getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);


    }

    @Test
    public void testModifySanction_OK(){
        UserModel admin = new UserModel("admin" + RandomStringGenerator.getAlphabeticString(10)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        admin.setAdmin(true);
        userService.saveUser(admin);
        setSecurityContext(admin);

        String mail = ("mail" + RandomStringGenerator.getAlphabeticString(10) + "@mail.austral.edu.ar");

        userService.saveUser(new UserModel(mail, "password", "Mail", "Mail", "12345678"));
        SanctionModel sanctionToChange = (SanctionModel) sanctionController.createSanction(new SanctionForm(mail, "reason", LocalDate.now().plus(Period.ofDays(20)))).getBody();

        assert sanctionToChange != null;
        sanctionToChange.setEndDate(sanctionToChange.getEndDate().plus(Period.ofDays(3)));

        assertThat(sanctionController.modifySanction(sanctionToChange).getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }

    @Test
    public void testModifySanction_BAD_REQUEST(){
        UserModel admin = new UserModel("admin" + RandomStringGenerator.getAlphabeticString(10)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        admin.setAdmin(true);
        userService.saveUser(admin);
        setSecurityContext(admin);

        String mail = ("mail" + RandomStringGenerator.getAlphabeticString(10) + "@mail.austral.edu.ar");

        userService.saveUser(new UserModel(mail, "password", "Mail", "Mail", "12345678"));
        SanctionModel sanctionToChange = (SanctionModel) sanctionController.createSanction(new SanctionForm(mail, "reason", LocalDate.now().plus(Period.ofDays(20)))).getBody();

        assert sanctionToChange != null;
        sanctionToChange.setEndDate(LocalDate.now().minus(Period.ofDays(5)));
        sanctionService.saveSanction(sanctionToChange);


        sanctionToChange.setEndDate(LocalDate.now().plus(Period.ofDays(3)));

        assertThat(sanctionController.modifySanction(sanctionToChange).getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    //test date before now
    public void testModifySanction_EXTECTATION_FAILED(){
        UserModel admin = new UserModel("admin" + RandomStringGenerator.getAlphabeticString(10)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        admin.setAdmin(true);
        userService.saveUser(admin);
        setSecurityContext(admin);

        String mail = ("mail" + RandomStringGenerator.getAlphabeticString(10) + "@mail.austral.edu.ar");

        userService.saveUser(new UserModel(mail, "password", "Mail", "Mail", "12345678"));
        SanctionModel sanctionToChange = (SanctionModel) sanctionController.createSanction(new SanctionForm(mail, "reason", LocalDate.now().plus(Period.ofDays(20)))).getBody();

        assert sanctionToChange != null;

        sanctionToChange.setEndDate(LocalDate.now().minus(Period.ofDays(4)));

        assertThat(sanctionController.modifySanction(sanctionToChange).getStatusCode()).isEqualByComparingTo(HttpStatus.EXPECTATION_FAILED);
    }

    @Test
    //test too future date
    public void testModifySanction_EXTECTATION_FAILED2(){
        UserModel admin = new UserModel("admin" + RandomStringGenerator.getAlphabeticString(10)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        admin.setAdmin(true);
        userService.saveUser(admin);
        setSecurityContext(admin);

        String mail = ("mail" + RandomStringGenerator.getAlphabeticString(10) + "@mail.austral.edu.ar");

        userService.saveUser(new UserModel(mail, "password", "Mail", "Mail", "12345678"));
        SanctionModel sanctionToChange = (SanctionModel) sanctionController.createSanction(new SanctionForm(mail, "reason", LocalDate.now().plus(Period.ofDays(20)))).getBody();

        assert sanctionToChange != null;

        sanctionToChange.setEndDate(LocalDate.now().plus(Period.ofWeeks(15)));

        assertThat(sanctionController.modifySanction(sanctionToChange).getStatusCode()).isEqualByComparingTo(HttpStatus.EXPECTATION_FAILED);
    }

    @Test
    public void testModifySanction_UNAUTHORIZED(){
        UserModel admin = new UserModel("admin" + RandomStringGenerator.getAlphabeticString(10)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        admin.setAdmin(true);
        userService.saveUser(admin);
        setSecurityContext(admin);

        String mail = ("mail" + RandomStringGenerator.getAlphabeticString(10) + "@mail.austral.edu.ar");

        userService.saveUser(new UserModel(mail, "password", "Mail", "Mail", "12345678"));
        SanctionModel sanctionToChange = (SanctionModel) sanctionController.createSanction(new SanctionForm(mail, "reason", LocalDate.now().plus(Period.ofDays(20)))).getBody();

        UserModel NOTadmin = new UserModel("NOTadmin" + RandomStringGenerator.getAlphabeticString(10)+ "@a.austral.edu.ar", "pass123", "Admin", "Admin", "12345678");
        admin.setAdmin(false);
        userService.saveUser(NOTadmin);
        setSecurityContext(NOTadmin);

        assert sanctionToChange != null;

        sanctionToChange.setEndDate(LocalDate.now().plus(Period.ofDays(3)));

        assertThat(sanctionController.modifySanction(sanctionToChange).getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }
}
