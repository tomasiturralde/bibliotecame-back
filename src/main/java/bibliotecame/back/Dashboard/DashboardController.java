package bibliotecame.back.Dashboard;

import bibliotecame.back.ErrorMessage;
import bibliotecame.back.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {


    private final UserService userService;
    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(UserService userService, DashboardService dashboardService) {
        this.userService = userService;
        this.dashboardService = dashboardService;
    }


    @GetMapping()
    public ResponseEntity getInformation(){

        if(!userService.findLogged().isAdmin()) return unauthorizedActionError();

        DashboardInformation dashboard = new DashboardInformation();

        long amountOfBooks = dashboardService.getAmountOfBooks();
        dashboard.setAmountOfBooks(amountOfBooks);

        int amountOfStudents = userService.findAllStudents().size();
        dashboard.setAmountOfStudents(amountOfStudents);

        int delayedLoans = dashboardService.getDelayedLoans();
        dashboard.setDelayedLoans(delayedLoans);

        int withdrawnLoans = dashboardService.getWithdrawalLoans();
        dashboard.setWithdrawnLoans(withdrawnLoans);

        int readyForWithdrawalLoans = dashboardService.getReadyForWithdrawalLoans();
        dashboard.setReadyForWithdrawalLoans(readyForWithdrawalLoans);

        List<Pair<String, Integer>> loansByMonth = dashboardService.getLoansByMonthOfLastYear();
        dashboard.setLoansByMonth(loansByMonth);

        List<BookDashboardDisplay> bestScoreDisplay = dashboardService.get5BestReviewed().stream().map(book ->
                new BookDashboardDisplay(book.getTitle(), book.getAuthor(), dashboardService.getAverageScore(book), dashboardService.getAmountOfLoans(book)))
                .collect(Collectors.toList());
        dashboard.setBestReviewed(bestScoreDisplay);

        List<BookDashboardDisplay> mostLoaned = dashboardService.get5MostLoaned().stream().map(book ->
                new BookDashboardDisplay(book.getTitle(), book.getAuthor(), dashboardService.getAverageScore(book), dashboardService.getAmountOfLoans(book)))
                .collect(Collectors.toList());
        dashboard.setMostLoaned(mostLoaned);



        return ResponseEntity.ok(dashboard);
    }

    private ResponseEntity unauthorizedActionError(){
        return new ResponseEntity<>(new ErrorMessage("¡Usted no está autorizado a realizar esta acción!"), HttpStatus.UNAUTHORIZED);
    }
}


