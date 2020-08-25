package bibliotecame.back.services;

import bibliotecame.back.models.UserModel;
import bibliotecame.back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserModel findUserById (int id){
        return this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("User with id: " + id + " not found!"));
    }

    public UserModel saveUser(UserModel userModel){
        return this.userRepository.save(userModel);
    }

    public boolean userIsAdmin(UserModel userModel){
        return userModel.isAdmin();
    }
}
