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

        UserModel user;
        try {
            user = this.userService.findLogged();
        } catch (RuntimeException e){
            return new ResponseEntity<>(id, HttpStatus.BAD_REQUEST);
        }

        if(user.getId() != id){
            return new ResponseEntity<>(id, HttpStatus.UNAUTHORIZED);
        }

        //todo: check prestamos activos, cuando existan.

        userService.deleteUser(user);

        return ResponseEntity.ok(id);
    }

    @GetMapping("user/getLogged")
    public ResponseEntity<UserModel> getLoggedUser(){
        UserModel userModel;
        try{
            userModel=userService.findLogged();
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(userModel,HttpStatus.OK);
    }

    @PutMapping("user/{id}/update")
    public ResponseEntity<UserModel> updateUser(@PathVariable Integer id, @RequestBody UserModel userModel){

        UserModel loggedUser;

        //It mustn't work if the user isn't loggedIn
        try {
            loggedUser = userService.findLogged();
        } catch (NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //It mustn't work if the Id from loggedUser differs from the one to modify, or if it tries to change its Id
        if(loggedUser.getId() != id){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //A user isn't allowed to modify its email
        if(!loggedUser.getEmail().equals(userModel.getEmail())){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(!userService.validUser(userModel))return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return ResponseEntity.ok(userService.updateUser(userModel,id));
    }
}
