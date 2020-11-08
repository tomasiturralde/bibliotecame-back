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

    private ResponseEntity unexistingRequestError(){
        return new ResponseEntity(new ErrorMessage("¡La solicitud pedida no existe!"),HttpStatus.BAD_REQUEST);
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
        if(!requestService.isValid(form)) return new ResponseEntity( new ErrorMessage("¡Debe completar correctamente los campos solicitados!"),HttpStatus.BAD_REQUEST);

        RequestModel request = new RequestModel(form);
        request.setUser(findLogged());
        request.setDate(LocalDate.now());
        request.setStatus(RequestStatus.PENDING);

        return new ResponseEntity(requestService.saveRequest(request), HttpStatus.OK);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity approveRequest(@PathVariable int id){
        if(!checkAdmin()) return unauthorizedActionError();
        try{
            RequestModel requestModel = requestService.findById(id);
            if(requestModel.getStatus().getId()!=0) return new ResponseEntity(new ErrorMessage("¡Esta solicitud ya fué evaluada!"),HttpStatus.BAD_REQUEST);
            requestModel.setStatus(RequestStatus.APPROVED);
            return new ResponseEntity(requestService.saveRequest(requestModel),HttpStatus.OK);
        } catch (RuntimeException e){
            return unexistingRequestError();
        }
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity rejectRequest(@PathVariable int id){
        if(!checkAdmin()) return unauthorizedActionError();
        try{
            RequestModel requestModel = requestService.findById(id);
            if(requestModel.getStatus().getId()!=0) return new ResponseEntity(new ErrorMessage("¡Esta solicitud ya fué evaluada!"),HttpStatus.BAD_REQUEST);
            requestModel.setStatus(RequestStatus.REJECTED);
            return new ResponseEntity(requestService.saveRequest(requestModel),HttpStatus.OK);
        } catch (RuntimeException e){
            return unexistingRequestError();
        }
    }

    @GetMapping()
    public ResponseEntity getAll(
            @Valid @RequestParam(value = "page") int page,
            @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @Valid @RequestParam(value = "search", required = false, defaultValue = "") String search
    ){
        if(size==0) size=10;
        if(!checkAdmin()) return unauthorizedActionError();
        return ResponseEntity.ok(requestService.findAllPaged(page,size,search.toLowerCase()));
    }

    @GetMapping("/user")
    public ResponseEntity getAllByUser(
            @Valid @RequestParam(value = "page") int page,
            @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @Valid @RequestParam(value = "search", required = false, defaultValue = "") String search
    ){
        if(size==0) size=10;
        if(checkAdmin()) return unauthorizedActionError();
        if(search.isEmpty()) return ResponseEntity.ok(requestService.findAllPagedByUser(page,size, findLogged()));
        else return ResponseEntity.ok(requestService.findAllPagedByUserAndFilter(page,size,findLogged(),search.toLowerCase()));
    }

    @GetMapping("/pending")
    public ResponseEntity getAllPending(
            @Valid @RequestParam(value = "page") int page,
            @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    ){
        if(size==0) size=10;
        if(!checkAdmin()) return unauthorizedActionError();
        return ResponseEntity.ok(requestService.findAllPagedByStatus(page,size,RequestStatus.PENDING));
    }

    @GetMapping("/{id}")
    public ResponseEntity getRequest(@PathVariable int id){
        try{
            RequestModel requestModel = requestService.findById(id);
            if(checkAdmin() || requestModel.getUser().getId() == findLogged().getId()) return ResponseEntity.ok(requestModel);
            return unauthorizedActionError();
        }catch (RuntimeException e){
            return new ResponseEntity(new ErrorMessage("¡La solicitud requerida no existe!"),HttpStatus.BAD_REQUEST);
        }
    }

}
