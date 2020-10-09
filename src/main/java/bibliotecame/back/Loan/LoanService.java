package bibliotecame.back.Loan;

import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Book.BookService;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookService bookService;
    private final UserService userService;

    @Autowired
    public LoanService(LoanRepository loanRepository, BookService bookService, UserService userService) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
        this.userService = userService;
    }

    public LoanModel saveLoan(LoanModel loanModel){
        return this.loanRepository.save(loanModel);
    }

    public LoanModel getLoanById(int loanId) throws NotFoundException {
        return this.loanRepository.findById(loanId).orElseThrow(() -> new NotFoundException("Loan not found"));
    }

    public List<LoanDisplay> getLoansPage(int page, int size, String search){
        List<LoanModel> list = findAll();

        list.sort((l1, l2) -> {
            if(l1.getReturnDate() == null && l2.getReturnDate() != null) return -1;
            if(l1.getReturnDate() != null && l2.getReturnDate() == null) return 1;
            return l1.getExpirationDate().compareTo(l2.getExpirationDate());
        });

        List<LoanDisplay> filtered = new ArrayList<>();
        if(!search.isEmpty()){
            String lookFor = search.toLowerCase();
            for (LoanModel loanModel : list) {
                if (filtered.size() == (page * size + size)) break;
                LoanDisplay display = turnLoanModalToDisplay(loanModel, Optional.of(userService.getUserFromLoan(loanModel)), true);
                if (display.getBookAuthor().toLowerCase().contains(lookFor) ||
                     display.getBookTitle().toLowerCase().contains(lookFor) ||
                     display.getUserEmail().toLowerCase().contains(lookFor) ||
                     display.getLoanStatus().getLabel().toLowerCase().contains(lookFor)) filtered.add(display);
            }
        } else{
            for (int i = 0; i <= page*size +size && i < list.size(); i++) {
                filtered.add(turnLoanModalToDisplay(list.get(i), Optional.of(userService.getUserFromLoan(list.get(i))), true));
            }
        }

        int start = page*size;
        int end = Math.min((start + size), filtered.size());
        return filtered.subList(start, end);
    }

    public LoanDisplay turnLoanModalToDisplay(LoanModel modal, Optional<UserModel> user, boolean withStatus){
        BookModel book = bookService.findBookByCopy(modal.getCopy());
        LoanDisplay display = user.map(userModel -> new LoanDisplay(modal.getId(),book.getTitle(), book.getAuthor(), modal.getExpirationDate(), modal.getReturnDate(), userModel.getEmail()))
                .orElseGet(() -> new LoanDisplay(modal.getId(),book.getTitle(), book.getAuthor(), modal.getExpirationDate(), modal.getReturnDate()));
        return withStatus? setLoanDisplayStatus(modal, display) : display;
    }

    public List<LoanModel> findAll(){
        return (List<LoanModel>) loanRepository.findAll();
    }

    public LoanDisplay setLoanDisplayStatus(LoanModel model, LoanDisplay display){
        if(model.getReturnDate() != null) display.setLoanStatus(LoanStatus.RETURNED);
        else if(model.getExtension() != null) display.setLoanStatus(LoanStatus.getFromInt(model.getExtension().getStatus().ordinal()));
        else if(model.getExpirationDate().isBefore(LocalDate.now())) display.setLoanStatus(LoanStatus.DELAYED);
        else if(model.getWithdrawalDate() != null) display.setLoanStatus(LoanStatus.WITHDRAWN);
        else display.setLoanStatus(LoanStatus.READY_FOR_WITHDRAWAL);
        return display;
    }

    public void deleteEveryExpiredLoan(){
        for(UserModel user : userService.getUsersWithLoans()){
            deleteExpirationLoansOfUsers(user);
        }
    }

    public void deleteExpirationLoansOfUsers(UserModel user){
        List<LoanModel> remainingLoans = new ArrayList<>();
        List<LoanModel> deletingLoans = new ArrayList<>();
        for(LoanModel loan: user.getLoans()){
            if(loan.getExpirationDate().isBefore(LocalDate.now()) && loan.getWithdrawalDate() == null){
                deletingLoans.add(loan);
            } else {
                remainingLoans.add(loan);
            }
        }
        user.setLoans(remainingLoans);
        userService.saveUser(user);
        for(LoanModel loan: deletingLoans){
            loanRepository.delete(loan);
        }
    }
}
