package bibliotecame.back.Sanction;

import bibliotecame.back.ErrorMessage;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Period;

@RestController
@RequestMapping("/sanction")
public class SanctionController {

    private final SanctionService sanctionService;
    private final UserService userService;

    @Autowired
    public SanctionController(SanctionService sanctionService, UserService userService) {
        this.sanctionService = sanctionService;
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity createSanction(@Valid @RequestBody SanctionForm sanctionForm){
        if(!checkAdmin()){
            return unauthorizedActionError();
        }
        return checkAndCreateSanction(sanctionForm);
    }

    public ResponseEntity checkAndCreateSanction(SanctionForm sanctionForm){

        UserModel user;

        if(!userService.emailExists(sanctionForm.getEmail())) return new ResponseEntity<>(new ErrorMessage("¡El email que ingresó no corresponde a ningún usuario!"),HttpStatus.BAD_REQUEST);

        user = userService.findUserByEmail(sanctionForm.getEmail());

        if(user.isAdmin()) return new ResponseEntity(new ErrorMessage("¡No puede sancionar a un administrador!"),HttpStatus.BAD_REQUEST);

        if(sanctionService.userIsSanctioned(user)) return new ResponseEntity<>(new ErrorMessage("¡El usuario ya esta sancionado!"),HttpStatus.UNPROCESSABLE_ENTITY);

        if(sanctionForm.getEndDate().isBefore(LocalDate.now())) return new ResponseEntity<>(new ErrorMessage("¡La sanción no puede terminar antes de la fecha actual!"),HttpStatus.EXPECTATION_FAILED);
        if(sanctionForm.getEndDate().isAfter(LocalDate.now().plus(Period.ofMonths(3)))) return new ResponseEntity<>(new ErrorMessage("¡La sanción no puede durar más de 3 meses!"),HttpStatus.EXPECTATION_FAILED);

        SanctionModel sanction = new SanctionModel(sanctionForm.getReason(), LocalDate.now(), sanctionForm.getEndDate(), user);

        return ResponseEntity.ok(this.sanctionService.saveSanction(sanction));
    }

    @PutMapping()
    public ResponseEntity modifySanction(@Valid @RequestBody SanctionModel sanctionModel){
        if(!checkAdmin()) return unauthorizedActionError();

        SanctionModel oldSanction = sanctionService.findSanctionById(sanctionModel.getId());

        if(oldSanction.getEndDate().isBefore(LocalDate.now())) return new ResponseEntity(new ErrorMessage("No puede modificar una sanción desactivada."),HttpStatus.BAD_REQUEST);
        if(sanctionModel.getEndDate().isBefore(LocalDate.now())) return new ResponseEntity<>(new ErrorMessage("¡La sanción no puede terminar antes de la fecha actual!"),HttpStatus.EXPECTATION_FAILED);
        if(sanctionModel.getEndDate().isAfter(LocalDate.now().plus(Period.ofMonths(3)))) return new ResponseEntity<>(new ErrorMessage("¡La sanción no puede durar más de 3 meses!"),HttpStatus.EXPECTATION_FAILED);

        oldSanction.setEndDate(sanctionModel.getEndDate());

        return ResponseEntity.ok(this.sanctionService.saveSanction(oldSanction));
    }

    private boolean checkAdmin(){
        return userService.findLogged().isAdmin();
    }

    private ResponseEntity unauthorizedActionError(){
        return new ResponseEntity<>(new ErrorMessage("¡Usted no está autorizado a realizar esta acción!"),HttpStatus.UNAUTHORIZED);
    }

}
