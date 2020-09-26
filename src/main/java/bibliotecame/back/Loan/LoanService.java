package bibliotecame.back.Loan;

import bibliotecame.back.User.UserModel;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public LoanModel saveLoan(LoanModel loanModel){
        return this.loanRepository.save(loanModel);
    }

    public LoanModel getLoanById(int loanId) throws NotFoundException {
        return this.loanRepository.findById(loanId).orElseThrow(() -> new NotFoundException("Loan not found"));
    }
}
