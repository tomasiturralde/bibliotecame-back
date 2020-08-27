package bibliotecame.back.services;

import bibliotecame.back.models.UserModel;
import bibliotecame.back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        return this.userRepository.save(userModel);
    }

    public boolean validUser(UserModel userModel){
        String passwordRegex = "^(?=.*?[A-Z]?)(?=.*?[a-z]?)(?=.*?[0-9]?).{6,}$";
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.austral.edu.)+[\\w-]{2,4}$";

        if(!userModel.getPassword().matches(passwordRegex)) return false;
        if(userModel.getPhoneNumber().isEmpty()) return false;
        if(userModel.getFirstName().isEmpty()) return false;
        if(userModel.getLastName().isEmpty()) return false;
        if(!userModel.getEmail().matches(emailRegex)) return false;

        return true;
    }

    public boolean emailExists(String email) {
        return this.userRepository.findByEmail(email).isPresent();
    }
}