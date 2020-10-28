package bibliotecame.back.Verification;

import bibliotecame.back.ErrorMessage;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationController {

    private final UserService userService;
    private final VerificationService verificationService;

    @Autowired
    public VerificationController(UserService userService, VerificationService verificationService) {
        this.userService = userService;
        this.verificationService = verificationService;
    }

    @PutMapping("/verify/{token}")
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
    @PutMapping("/reset/{token}")
    public ResponseEntity resetPassword(@PathVariable String token, @RequestBody PasswordContainer passwordContainer){
        try{
            VerificationModel verificationModel = verificationService.findVerificationByToken(token);
            if(verificationModel.getToken().length()<=40) return new ResponseEntity(new ErrorMessage("¡El token enviado no es un token de reinicio de contraseña valido!"),HttpStatus.BAD_REQUEST);
            UserModel user = verificationModel.getUserModel();
            String passwordRegex = "^(?=.*\\d)(?=.*[a-zA-Z])([a-zA-Z0-9]+){7,}$";
            if(passwordContainer.getPassword().matches(passwordRegex)) return new ResponseEntity(new ErrorMessage("¡La contraseña ingresada no es valida, por favor intente con otra!"), HttpStatus.BAD_REQUEST);
            user.setPassword(passwordContainer.getPassword());
            userService.saveUser(user);
            verificationService.deleteVerification(verificationModel);
            return ResponseEntity.ok(user);
        }catch (RuntimeException e){
            return new ResponseEntity(new ErrorMessage("¡El token solicitado no es valido!"), HttpStatus.BAD_REQUEST);
        }
    }

}
