package bibliotecame.back.Request;

import bibliotecame.back.ErrorMessage;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
