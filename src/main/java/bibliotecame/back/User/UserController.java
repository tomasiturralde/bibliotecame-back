package bibliotecame.back.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("{id}")
    public ResponseEntity<UserModel> getUserModel(@PathVariable Integer id){
        return new ResponseEntity<>(this.userService.findUserById(id), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UserModel> createUser(@Valid @RequestBody UserModel userModel){

        if(!userService.validUser(userModel))return new ResponseEntity<>(userModel, HttpStatus.BAD_REQUEST);

        if(userService.emailExists(userModel.getEmail())) return new ResponseEntity<>(userModel, HttpStatus.BAD_REQUEST);
        userModel.setAdmin(false);

        return ResponseEntity.ok(userService.saveUser(userModel));

    }

    @PutMapping("{id}/update")
    public ResponseEntity<UserModel> updateUser(@PathVariable Integer id, @RequestBody UserModel userModel){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel loggedUser;
        //It mustn't work if the user isn't loggedIn
        try {
            loggedUser = (UserModel) authentication.getPrincipal();
        } catch (NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //It mustn't work if the Id from loggedUser differs from the one to modify, or if it tries to change its Id
        if(loggedUser.getId() != id || userModel.getId()!=loggedUser.getId()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //A user isn't allowed to modify its email
        if(!loggedUser.getEmail().equals(userModel.getEmail())){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(!userService.validUser(userModel))return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return ResponseEntity.ok(userService.saveUser(userModel));
    }
}
