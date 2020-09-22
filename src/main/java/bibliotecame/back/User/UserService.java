package bibliotecame.back.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserModel findUserById (int id){
        return this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("User with id: " + id + " not found!"));
    }

    public UserModel findUserByEmail(String email){
        return this.userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User with email: " + email + " not found!"));
    }


    public UserModel saveUser(UserModel userModel){
        userModel.setPassword(BCrypt.hashpw(userModel.getPassword(), BCrypt.gensalt()));
        return this.userRepository.save(userModel);
    }

    public UserModel updateUser(UserModel userModel,int id){
        userModel.setPassword(BCrypt.hashpw(userModel.getPassword(), BCrypt.gensalt()));
        UserModel userToUpdate = findUserById(id);
        userToUpdate.setPassword(userModel.getPassword());
        userToUpdate.setFirstName(userModel.getFirstName());
        userToUpdate.setLastName(userModel.getLastName());
        userToUpdate.setPhoneNumber(userModel.getPhoneNumber());
        return this.userRepository.save(userToUpdate);
    }

    public boolean validUser(UserModel userModel){
        String passwordRegex = "^[a-zA-Z0-9].{6,}$";
        String emailRegex = "^[\\w-.]+@([\\w-]+\\.austral.edu.)+[\\w-]{2,4}$";

        if(userModel.getPhoneNumber().isEmpty()) return false;
        if(userModel.getFirstName().isEmpty()) return false;
        if(userModel.getLastName().isEmpty()) return false;
        if(!userModel.getPassword().matches(passwordRegex)) return false;
        return userModel.getEmail().matches(emailRegex);
    }

    public UserModel findLogged(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
        return findUserByEmail(email);
    }

    public boolean emailExists(String email) {
        return this.userRepository.findByEmail(email).isPresent();
    }

    public void deleteUser(UserModel user){
        this.userRepository.delete(user);
    }

    public boolean userExists(int id) {return this.userRepository.findById(id).isPresent(); }
}
