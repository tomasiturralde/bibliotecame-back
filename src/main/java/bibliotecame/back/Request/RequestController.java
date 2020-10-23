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
}
