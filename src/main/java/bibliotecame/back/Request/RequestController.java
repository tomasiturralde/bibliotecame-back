package bibliotecame.back.Request;

import bibliotecame.back.ErrorMessage;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/request")
public class RequestController {
    private final UserService userService;
    private final RequestService requestService;

    @Autowired
    public RequestController(UserService userService, RequestService requestService) {
        this.userService = userService;
        this.requestService = requestService;
    }

    private ResponseEntity unauthorizedActionError(){
        return new ResponseEntity<>(new ErrorMessage("¡Usted no está autorizado a realizar esta acción!"), HttpStatus.UNAUTHORIZED);
    }

    private UserModel findLogged(){
        return userService.findLogged();
    }

    private boolean checkAdmin(){
        return findLogged().isAdmin();
    }

    @PostMapping()
    public ResponseEntity createRequest(@Valid @RequestBody RequestForm form){
        if(checkAdmin()) return unauthorizedActionError();

        RequestModel request = new RequestModel(form);
        request.setUser(findLogged());
        request.setDate(LocalDate.now());
        request.setStatus(RequestStatus.PENDING);

        return new ResponseEntity(requestService.saveRequest(request), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity getAll(
            @Valid @RequestParam(value = "page") int page,
            @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    ){
        if(size==0) size=10;
        if(!checkAdmin()) return unauthorizedActionError();
        return ResponseEntity.ok(requestService.findAll(page,size));
    }

    @GetMapping("/pending")
    public ResponseEntity getAllPending(
            @Valid @RequestParam(value = "page") int page,
            @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    ){
        if(size==0) size=10;
        if(!checkAdmin()) return unauthorizedActionError();
        return ResponseEntity.ok(requestService.findAllByStatus(page,size,RequestStatus.PENDING));
    }

    @GetMapping("/{id}")
    public ResponseEntity getRequest(@PathVariable int id){
        if(!checkAdmin()) return unauthorizedActionError();
        try{
            RequestModel requestModel = requestService.findById(id);
            return ResponseEntity.ok(requestModel);
        }catch (RuntimeException e){
            return new ResponseEntity(new ErrorMessage("¡La solicitud requerida no existe!"),HttpStatus.BAD_REQUEST);
        }
    }

}
