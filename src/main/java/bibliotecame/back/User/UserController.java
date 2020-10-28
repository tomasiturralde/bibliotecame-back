package bibliotecame.back.User;

import bibliotecame.back.ErrorMessage;
import bibliotecame.back.Verification.VerificationModel;
import bibliotecame.back.Verification.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    private final UserService userService;
    private final VerificationService verificationService;

    @Autowired
    public UserController(UserService userService, VerificationService verificationService){
        this.userService = userService;
        this.verificationService = verificationService;
    }

    @GetMapping("user/{id}")
    public ResponseEntity getUserModel(@PathVariable Integer id){
        if(!userService.userExists(id)) return new ResponseEntity<>(new ErrorMessage("¡El usuario no existe!"),HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(this.userService.findUserById(id), HttpStatus.OK);
    }

    @GetMapping("users")
    public ResponseEntity getUsers(@Valid @RequestParam(value = "search") String search){
        if(!userService.findLogged().isAdmin()) return new ResponseEntity(new ErrorMessage("¡Usted no está autorizado a realizar esta acción!"),HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(this.userService.getAllByEmailSearch(search), HttpStatus.OK);
    }

    @PostMapping(value = "/signup")
    public ResponseEntity createUser(@Valid @RequestBody UserModel userModel){

        if(!userService.validUser(userModel))return new ResponseEntity<>(new ErrorMessage("¡Por favor, verifique los datos enviados!"), HttpStatus.BAD_REQUEST);

        if(userService.emailExists(userModel.getEmail())) return new ResponseEntity<>(new ErrorMessage("¡Usted ya está registrado!"), HttpStatus.BAD_REQUEST);
        userModel.setAdmin(false);
        userModel.setVerified(false);
        UserModel savedUser = userService.saveUser(userModel);

        VerificationModel verificationModel = new VerificationModel(userModel);
        verificationService.saveVerification(verificationModel);

        return ResponseEntity.ok(savedUser);

    }

    @DeleteMapping("deleteUser/{id}")
    public ResponseEntity deleteUser(@PathVariable Integer id){

        UserModel user;
        try {
            user = this.userService.findLogged();
        } catch (RuntimeException e){
            return new ResponseEntity<>(new ErrorMessage("¡El usuario que quiere eliminar no existe!"), HttpStatus.BAD_REQUEST);
        }

        if(user.getId() != id){
            return new ResponseEntity<>(new ErrorMessage("¡Usted no puede eliminar la cuenta de otro usuario!"), HttpStatus.UNAUTHORIZED);
        }

        if(!userService.getActiveLoans(user).isEmpty()) return new ResponseEntity<>(new ErrorMessage("¡No puede eliminar su cuenta teniendo prestamos activos!"), HttpStatus.BAD_REQUEST);

        userService.deleteUser(user);

        return ResponseEntity.ok(id);
    }

    @GetMapping("user/getLogged")
    public ResponseEntity getLoggedUser(){
        UserModel userModel;
        try{
            userModel=userService.findLogged();
        }
        catch (Exception e){
            return new ResponseEntity<>(new ErrorMessage("¡Debes iniciar sesión primero!"),HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(userModel,HttpStatus.OK);
    }

    @PutMapping("user/{id}/update")
    public ResponseEntity updateUser(@PathVariable Integer id, @RequestBody UserModel userModel){

        UserModel loggedUser;

        //It mustn't work if the user isn't loggedIn
        try {
            loggedUser = userService.findLogged();
        } catch (NullPointerException e) {
            return new ResponseEntity<>(new ErrorMessage("¡Por favor, inicie sesión para poder modificar sus datos!"),HttpStatus.UNAUTHORIZED);
        }

        //It mustn't work if the Id from loggedUser differs from the one to modify, or if it tries to change its Id
        if(loggedUser.getId() != id){
            return new ResponseEntity<>(new ErrorMessage("¡Usted no puede modificar los datos de otro usuario!"),HttpStatus.UNAUTHORIZED);
        }

        //A user isn't allowed to modify its email
        if(!loggedUser.getEmail().equals(userModel.getEmail())){
            return new ResponseEntity<>(new ErrorMessage("¡Usted no puede modificar su correo electrónico!"),HttpStatus.UNAUTHORIZED);
        }

        if(!userService.validUser(userModel))return new ResponseEntity<>(new ErrorMessage("¡Por favor, verifique los datos ingresados!"),HttpStatus.BAD_REQUEST);

        return ResponseEntity.ok(userService.updateUser(userModel,id));
    }

    @PostMapping("user/forgot/{email}")
    public ResponseEntity forgotPassword(@PathVariable String email){
        try{
            userService.findLogged();
            return new ResponseEntity(new ErrorMessage("¡Para modificar su contraseña dirijase a su perfil!"),HttpStatus.BAD_REQUEST);
        }catch (RuntimeException e){}
        UserModel user;
        try{
            user = userService.findUserByEmail(email);
            if(!user.isVerified()) return new ResponseEntity(new ErrorMessage("¡Debe verificar su dirección de correo para que podamos enviarle instrucciones!"),HttpStatus.UNAUTHORIZED);
            VerificationModel verification = new VerificationModel(user);
            verification.setToken(verification.getToken()+"password");
            verificationService.savePasswordVerification(verification);
            return ResponseEntity.ok(email);
        }catch (RuntimeException e){
            return new ResponseEntity(new ErrorMessage("¡El email solicitado no pertenece a ninguna cuenta!"),HttpStatus.BAD_REQUEST);
        }
    }

}
