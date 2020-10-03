package bibliotecame.back.Extension;

import bibliotecame.back.Loan.LoanModel;
import bibliotecame.back.Loan.LoanService;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ExtensionService {

    private final ExtensionRepository extensionRepository;

    private final LoanService loanService;

    private final UserService userService;

    private static final int extensionDays = 3;

    @Autowired
    public ExtensionService(ExtensionRepository extensionRepository, LoanService loanService, UserService userService) {
        this.extensionRepository = extensionRepository;
        this.loanService = loanService;
        this.userService = userService;
    }

    public ExtensionModel findById(int id){
        return extensionRepository.findById(id).orElseThrow(() -> new RuntimeException("Extension with id: " + id + " doesn't exist"));
    }

    public ExtensionModel saveExtension(ExtensionModel extensionModel){
        return extensionRepository.save(extensionModel);
    }

    public ResponseEntity<ExtensionModel> createExtension(int loanId) {
        LoanModel loan;

        try {
            loan = this.loanService.getLoanById(loanId);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        UserModel user = userService.findLogged();

        if (user.isAdmin()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!loanIsValid(loan) || !userCanMakeExtension(user, loan)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final ExtensionModel extension = new ExtensionModel(LocalDate.now());
        saveExtension(extension);
        loan.setExtension(extension);
        loanService.saveLoan(loan);

        return new ResponseEntity<>(extension ,HttpStatus.OK);
    }

    private boolean loanIsValid(LoanModel loan){
        return loan.getExtension() == null;
    }

    private boolean userCanMakeExtension(UserModel user, LoanModel loan) {
        return userService.getDelayedLoans(user).isEmpty() && user.getLoans().stream().anyMatch(x -> x.getId() == loan.getId());
    }


}
