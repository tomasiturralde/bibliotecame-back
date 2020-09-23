package bibliotecame.back.Loan;

import bibliotecame.back.User.UserModel;
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
}
