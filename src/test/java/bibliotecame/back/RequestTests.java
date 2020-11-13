package bibliotecame.back;

import bibliotecame.back.Book.BookRepository;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Request.*;
import bibliotecame.back.Tag.TagRepository;
import bibliotecame.back.Tag.TagService;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class RequestTests {

    @Mock
    private TagService tagService;
    @Autowired
    private TagRepository tagRepository;

    @Mock
    private BookService bookService;
    @Autowired
    private BookRepository bookRepository;

    @Mock
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Mock
    private RequestService requestService;
    @Autowired
    private RequestRepository requestRepository;
    @Mock
    private RequestController requestController;

    Authentication authentication;
    SecurityContext securityContext;

    UserModel admin;
    UserModel nonAdmin;


    @BeforeAll
    void setUp(){
        bookService = new BookService(bookRepository, tagService);
        userService = new UserService(userRepository,bookService);
        tagService = new TagService(tagRepository);
        requestService = new RequestService(requestRepository);
        requestController = new RequestController(userService,requestService);

        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);

        admin = new UserModel(RandomStringGenerator.getAlphabeticString(10)+"@ing.austral.edu.ar", "password123", RandomStringGenerator.getAlphabeticString(10), RandomStringGenerator.getAlphabeticString(10), "12341234");
        admin.setAdmin(true);
        userRepository.save(admin);
        nonAdmin = new UserModel(RandomStringGenerator.getAlphabeticString(10)+"@ing.austral.edu.ar", "password123", RandomStringGenerator.getAlphabeticString(10), RandomStringGenerator.getAlphabeticString(10), "12341234");
        nonAdmin.setAdmin(false);
        userRepository.save(nonAdmin);
    }

    private void setSecurityContext(UserModel user){
        List<GrantedAuthority> auths = new ArrayList<>();
        User securityUser = new User(user.getEmail(), user.getPassword(), auths);
        Mockito.when(authentication.getPrincipal()).thenReturn(securityUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void userCanCreateARequest(){
        setSecurityContext(nonAdmin);
        RequestForm form = new RequestForm("Head-First design patterns",2004,"Eric Freeman & Elisabeth Robson","O'Reilly","Es un muy buen libro para ampliar conocimientos sobre patrones de diseño.");
        ResponseEntity responseEntity = requestController.createRequest(form);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((RequestModel) responseEntity.getBody()).getAuthor()).isEqualTo(form.getAuthor());
    }

    @Test
    public void adminCantCreateARequest(){
        setSecurityContext(admin);
        RequestForm form = new RequestForm("Head-First design patterns",2004,"Eric Freeman & Elisabeth Robson","O'Reilly","Es un muy buen libro para ampliar conocimientos sobre patrones de diseño.");
        ResponseEntity responseEntity = requestController.createRequest(form);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void adminCanGetARequest(){
        setSecurityContext(nonAdmin);
        RequestForm form = new RequestForm("Head-First design patterns",2004,"Eric Freeman & Elisabeth Robson","O'Reilly","Es un muy buen libro para ampliar conocimientos sobre patrones de diseño.");
        ResponseEntity responseEntity = requestController.createRequest(form);
        setSecurityContext(admin);
        int id = ((RequestModel) responseEntity.getBody()).getId();
        assertThat(((RequestModel)requestController.getRequest(id).getBody()).getTitle()).isEqualTo("Head-First design patterns");
        assertThat(((Page<RequestModel>)requestController.getAll(0,10,"").getBody()).getTotalElements()).isGreaterThan(0);
    }

    @Test
    public void userCanGetARequest(){
        setSecurityContext(nonAdmin);
        RequestForm form = new RequestForm("Head-First design patterns",2004,"Eric Freeman & Elisabeth Robson","O'Reilly","Es un muy buen libro para ampliar conocimientos sobre patrones de diseño.");
        ResponseEntity responseEntity = requestController.createRequest(form);

        int id = ((RequestModel) responseEntity.getBody()).getId();
        assertThat(((RequestModel)requestController.getRequest(id).getBody()).getTitle()).isEqualTo("Head-First design patterns");
    }

    @Test
    public void adminCanApproveAndRejectARequestThatIsPending(){
        setSecurityContext(nonAdmin);
        RequestForm form = new RequestForm("Head-First design patterns",2004,"Eric Freeman & Elisabeth Robson","O'Reilly","Es un muy buen libro para ampliar conocimientos sobre patrones de diseño.");
        ResponseEntity responseEntity1 = requestController.createRequest(form);
        form.setTitle("Head-First tail recursion");
        ResponseEntity responseEntity2 = requestController.createRequest(form);
        setSecurityContext(admin);
        assertThat(requestController.approveRequest(((RequestModel)responseEntity1.getBody()).getId()).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(requestController.rejectRequest(((RequestModel)responseEntity2.getBody()).getId()).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(requestController.approveRequest(((RequestModel)responseEntity1.getBody()).getId()).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);


    }

    @Test
    public void userCanGetRequests(){
        setSecurityContext(nonAdmin);
        RequestForm form = new RequestForm("Head-First design patterns",2004,"Eric Freeman & Elisabeth Robson","O'Reilly","Es un muy buen libro para ampliar conocimientos sobre patrones de diseño.");
        ResponseEntity responseEntity = requestController.createRequest(form);

        int id = ((RequestModel) responseEntity.getBody()).getId();
        assertThat(((Page<RequestDisplay>)requestController.getAllByUser(0,10,"").getBody()).getContent().stream()).anyMatch(requestModel -> requestModel.getId() == id);
    }

    @Test
    public void userCanGetRequestsWithFilter(){
        setSecurityContext(nonAdmin);
        RequestForm form = new RequestForm("Head-First design patterns",2004,"Eric Freeman & Elisabeth Robson","O'Reilly","Es un muy buen libro para ampliar conocimientos sobre patrones de diseño.");
        ResponseEntity responseEntity = requestController.createRequest(form);

        int id = ((RequestModel) responseEntity.getBody()).getId();
        assertThat(((Page<RequestDisplay>)requestController.getAllByUser(0,10,"Eric Freeman").getBody()).getTotalElements()).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void adminCanGetRequestsWithFilter(){
        setSecurityContext(nonAdmin);
        RequestForm form = new RequestForm("Head-First design patterns",2004,"Eric Freeman & Elisabeth Robson","O'Reilly","Es un muy buen libro para ampliar conocimientos sobre patrones de diseño.");
        requestController.createRequest(form);
        setSecurityContext(admin);

        assertThat(((Page<RequestDisplay>)requestController.getAll(0,10,"Eric").getBody()).getTotalElements()).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void adminCantModifyAnUnexistingRequest(){
        setSecurityContext(admin);
        assertThat(requestController.rejectRequest(-1).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(requestController.approveRequest(-1).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testGetAllPending(){
        setSecurityContext(nonAdmin);
        assertThat(requestController.getAllPending(0,10).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        setSecurityContext(admin);
        assertThat(requestController.getAllPending(0,10).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testGetRequest(){
        setSecurityContext(nonAdmin);
        assertThat(requestController.getRequest(-1).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        setSecurityContext(admin);
        RequestDisplay requestDisplay = ((Page<RequestDisplay>)requestController.getAll(0,10,"").getBody()).getContent().get(0);
        setSecurityContext(nonAdmin);
        assertThat(requestController.getRequest(requestDisplay.getId()).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testRequestDisplayMethods() {
        RequestDisplay disp = new RequestDisplay();
        disp.setAuthor("Author");
        disp.setDate(LocalDate.now());
        disp.setStatus(RequestStatus.getFromInt(1));
        disp.setTitle("Title");
        disp.setUserEmail("Email@ing.austral.edu.ar");
        assertThat(disp.getAuthor()).isEqualTo("Author");
        assertThat(disp.getDate()).isBefore(LocalDate.now().plus(Period.ofDays(10)));
        assertThat(disp.getUserEmail()).contains("austral");
        assertThat(disp.getTitle()).isEqualTo("Title");
        assertThat(disp.getStatus().getId()).isEqualTo(1);
    }

    @Test
    public void testRequestModelMethods() {
        RequestModel rm = new RequestModel("Title",1998,"Author","Publisher","Reason");
        rm.setId(20);
        rm.setTitle("Titulo");
        rm.setYear(20);
        rm.setAuthor(rm.getYear()+rm.getPublisher());
        rm.setPublisher("Editorial");
        rm.setReason("He filled this form in spanish!");
        assertThat(rm.getAuthor()).contains("20");
        assertThat(rm.getReason()).contains("pan");
    }

    @Test
    public void testRequestFormMethods(){
        RequestForm rm = new RequestForm("Titulo","Autor","RAZONAMIENTO!");
        assertThat(rm.getPublisher()).isEqualTo(null);
        rm.setPublisher("Editorial");
        rm.setYear(1998);
        assertThat(rm.getYear()).isLessThan(2000);
    }

}
