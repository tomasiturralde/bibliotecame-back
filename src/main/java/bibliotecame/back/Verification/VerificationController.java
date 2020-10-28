package bibliotecame.back.Verification;

import bibliotecame.back.ErrorMessage;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verify")
public class VerificationController {

    private final UserService userService;
    private final VerificationService verificationService;

    @Autowired
    public VerificationController(UserService userService, VerificationService verificationService) {
        this.userService = userService;
        this.verificationService = verificationService;
    }

    @PutMapping("/{token}")
    public ResponseEntity verifyAccount(@PathVariable String token){
        try{
            VerificationModel verificationModel = verificationService.findVerificationByToken(token);
            UserModel user = verificationModel.getUserModel();
            user.setVerified(true);
            userService.saveWithoutEncryption(user);
            verificationService.deleteVerification(verificationModel);
            return ResponseEntity.ok(user);
        }catch (RuntimeException e){
            return new ResponseEntity(new ErrorMessage("¡El token solicitado no es valido!"), HttpStatus.BAD_REQUEST);
        }
    }

}
