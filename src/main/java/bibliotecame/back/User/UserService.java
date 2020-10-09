package bibliotecame.back.User;

import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.Loan.LoanModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final BookService bookService;

    @Autowired
    public UserService(UserRepository userRepository, BookService bookService) {
        this.userRepository = userRepository;
        this.bookService = bookService;
    }

    public UserModel findUserById (int id){
        return this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("User with id: " + id + " not found!"));
    }

    public UserModel findUserByEmail(String email){
        return this.userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User with email: " + email + " not found!"));
    }

    public List<UserModel> getAllByEmailSearch(String search){

        return this.userRepository.findAllByEmail(search.toLowerCase());
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
        String passwordRegex = "^[a-zA-Z0-9]{6,}$";
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

    public List<LoanModel> getActiveLoans(UserModel user){
        List<LoanModel> actives = new ArrayList<>();
        for(LoanModel loan : user.getLoans()){
            if(loan.getReturnDate() == null){
                actives.add(loan);
            }
        }
        return actives;
    }

    public List<LoanModel> getReturnedLoansPage(int page, int size, UserModel user){
        List<LoanModel> returned = new ArrayList<>();
        for(LoanModel loan : user.getLoans()){
            if(loan.getReturnDate() != null){
                returned.add(loan);
            }
        }
        returned.sort((l0, l1) -> l1.getReturnDate().compareTo(l0.getReturnDate()));

        int start = page*size;
        int end = Math.min((start + size), returned.size());
        return returned.subList(start, end);
    }

    public boolean hasLoanOfBook(UserModel user, BookModel book){
        List<LoanModel> actives = getActiveLoans(user);
        for(LoanModel loan : actives){
            if(bookService.containsCopy(book, loan.getCopy())){
                return true;
            }
        }
        return false;
    }

    public List<LoanModel> getDelayedLoans(UserModel user){
        List<LoanModel> delayed = new ArrayList<>();
        for(LoanModel loan : user.getLoans()){
            if(loan.getExpirationDate().isBefore(LocalDate.now()) && loan.getReturnDate() == null){
                delayed.add(loan);
            }
        }
        return delayed;
    }

    public void addLoan(UserModel user, LoanModel loan){
        List<LoanModel> previousLoans = new ArrayList<>(user.getLoans());
        previousLoans.add(loan);
        user.setLoans(previousLoans);

        userRepository.save(user);
    }

    public boolean emailExists(String email) {
        return this.userRepository.findByEmail(email).isPresent();
    }

    public void deleteUser(UserModel user){
        this.userRepository.delete(user);
    }

    public boolean userExists(int id) {return this.userRepository.findById(id).isPresent(); }

    public boolean loanIsOfUser(UserModel user, LoanModel loan){
        for(LoanModel l : user.getLoans()){
            if(loan.getId() == l.getId()){
                return true;
            }
        }
        return false;
    }

    public UserModel getUserFromLoan(LoanModel loan){
        List<UserModel> users = (List<UserModel>) userRepository.findAll();
        for(UserModel user : users){
            if(loanIsOfUser(user, loan)){
                return user;
            }
        }
        throw new RuntimeException("Loan has no user.");
    }

    public List<UserModel> getUsersWithLoans(){
        List<UserModel> users = new ArrayList<>();
        for(UserModel user : userRepository.findAll()){
            if(!user.getLoans().isEmpty()){
                users.add(user);
            }
        }
        return users;
    }

}
