package bibliotecame.back.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("user/{id}")
    public ResponseEntity getUserModel(@PathVariable Integer id){
        if(!userService.userExists(id)) return new ResponseEntity<>("¡El usuario solicitado no existe!",HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(this.userService.findUserById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/signup")
    public ResponseEntity createUser(@Valid @RequestBody UserModel userModel){

        if(!userService.validUser(userModel))return new ResponseEntity<>("¡Verifique los datos ingresados!", HttpStatus.BAD_REQUEST);

        if(userService.emailExists(userModel.getEmail())) return new ResponseEntity<>("¡Este correo electrónico ya está registrado!", HttpStatus.BAD_REQUEST);
        userModel.setAdmin(false);

        return ResponseEntity.ok(userService.saveUser(userModel));

    }

    @DeleteMapping("deleteUser/{id}")
    public ResponseEntity deleteUser(@PathVariable Integer id){

        UserModel user;
        try {
            user = this.userService.findLogged();
        } catch (RuntimeException e){
            return new ResponseEntity<>("¡Debe iniciar sesión para poder eliminar su cuenta!", HttpStatus.BAD_REQUEST);
        }

        if(user.getId() != id){
            return new ResponseEntity<>("¡Usted no puede eliminar la cuenta de otro usuario!", HttpStatus.UNAUTHORIZED);
        }

        if(!userService.getActiveLoans(user).isEmpty()) return new ResponseEntity<>("¡Devuelva sus prestamos activos antes de eliminar su cuenta!", HttpStatus.BAD_REQUEST);

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
            return new ResponseEntity<>("¡Usted no esta autorizado a realizar esta acción!",HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(userModel,HttpStatus.OK);
    }

    @PutMapping("user/{id}/update")
    public ResponseEntity updateUser(@PathVariable Integer id, @RequestBody UserModel userModel){

        UserModel loggedUser;

        try {
            loggedUser = userService.findLogged();
        } catch (NullPointerException e) {
            return new ResponseEntity<>("¡No puede modificar sus datos si no inicia sesión!",HttpStatus.UNAUTHORIZED);
        }

        if(loggedUser.getId() != id){
            return new ResponseEntity<>("¡Usted no puede modificar los datos de otro usuario!",HttpStatus.UNAUTHORIZED);
        }

        if(!loggedUser.getEmail().equals(userModel.getEmail())){
            return new ResponseEntity<>("¡No tiene permitido modificar su dirección de correo!",HttpStatus.UNAUTHORIZED);
        }

        if(!userService.validUser(userModel))return new ResponseEntity<>("¡Verifique los datos ingresados!",HttpStatus.BAD_REQUEST);

        return ResponseEntity.ok(userService.updateUser(userModel,id));
    }
}
