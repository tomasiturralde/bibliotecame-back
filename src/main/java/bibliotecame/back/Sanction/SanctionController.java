package bibliotecame.back.Sanction;

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
    public ResponseEntity<SanctionModel> createSanction(@Valid @RequestBody SanctionForm sanctionForm){
        if(!checkAdmin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return checkAndCreateSanction(sanctionForm);
    }

    public ResponseEntity<SanctionModel> checkAndCreateSanction(SanctionForm sanctionForm){

        UserModel user;

        if(!userService.emailExists(sanctionForm.getEmail())) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        user = userService.findUserByEmail(sanctionForm.getEmail());

        if(sanctionService.userIsSanctioned(user)) return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);

        if(sanctionForm.getEndDate().isBefore(LocalDate.now()) ||
            sanctionForm.getEndDate().isAfter(LocalDate.now().plus(Period.ofMonths(3)))) return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);

        SanctionModel sanction = new SanctionModel(sanctionForm.getReason(), LocalDate.now(), sanctionForm.getEndDate(), user);

        return ResponseEntity.ok(this.sanctionService.saveSanction(sanction));
    }

    private boolean checkAdmin(){
        return userService.findLogged().isAdmin();
    }
}
