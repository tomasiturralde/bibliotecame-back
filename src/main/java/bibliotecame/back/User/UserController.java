package bibliotecame.back.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<UserModel> getUserModel(@PathVariable Integer id){
        return new ResponseEntity<>(this.userService.findUserById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<UserModel> createUser(@Valid @RequestBody UserModel userModel){

        if(!userService.validUser(userModel))return new ResponseEntity<>(userModel, HttpStatus.BAD_REQUEST);

        if(userService.emailExists(userModel.getEmail())) return new ResponseEntity<>(userModel, HttpStatus.BAD_REQUEST);
        userModel.setAdmin(false);

        return ResponseEntity.ok(userService.saveUser(userModel));

    }

    @DeleteMapping("deleteUser/{id}")
    public ResponseEntity<Integer> deleteUser(@PathVariable Integer id){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel loggedIn = (UserModel) authentication.getPrincipal();

        UserModel user;
        try {
            user = this.userService.findUserById(id);
        } catch (RuntimeException e){
            return new ResponseEntity<>(id, HttpStatus.BAD_REQUEST);
        }

        if(loggedIn.getId() != id){
            return new ResponseEntity<>(id, HttpStatus.UNAUTHORIZED);
        }

        //todo: check prestamos activos, cuando existan.

        userService.deleteUser(user);

        return ResponseEntity.ok(id);
    }
}
