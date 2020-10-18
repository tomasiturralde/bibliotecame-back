package bibliotecame.back.Extension;

import bibliotecame.back.ErrorMessage;
import bibliotecame.back.Loan.LoanModel;
import bibliotecame.back.Loan.LoanService;
import bibliotecame.back.User.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/extension")
public class ExtensionController {

    private final ExtensionService extensionService;
    private final UserService userService;
    private final LoanService loanService;

    @Autowired
    public ExtensionController(ExtensionService extensionService, UserService userService, LoanService loanService) {
        this.extensionService = extensionService;
        this.userService = userService;
        this.loanService = loanService;
    }

    @PostMapping("/{loanId}")
    public ResponseEntity createExtension(@PathVariable int loanId){
            return extensionService.createExtension(loanId);
    }

    @PutMapping("/{loanId}/approve")
    public ResponseEntity approveExtension(@PathVariable int loanId){

        if(!userService.findLogged().isAdmin()) return unauthorizedActionError();
        try{
            LoanModel loan = loanService.getLoanById(loanId);
            ExtensionModel extension = loan.getExtension();
            if(extension==null) throw new NotFoundException("loan has no extension");
            ResponseEntity response = modifyExtension(extension, ExtensionStatus.APPROVED);
            if (response.getStatusCode() == HttpStatus.OK) modifyLoanExpirationDate(loan);
            return response;
        }
        catch (NotFoundException e) {
            return new ResponseEntity(new ErrorMessage("¡La extensión solicitada no existe!"),HttpStatus.BAD_REQUEST);
        }

    }

    private void modifyLoanExpirationDate(LoanModel loan) {
        loan.setExpirationDate(loan.getExpirationDate().plusDays(3));
        loanService.saveLoan(loan);
    }

    @PutMapping("/{loanId}/reject")
    public ResponseEntity rejectExtension(@PathVariable int loanId){

        if(!userService.findLogged().isAdmin()) return unauthorizedActionError();
        try{
            ExtensionModel extension = loanService.getLoanById(loanId).getExtension();
            if(extension==null) throw new NotFoundException("loan has no extension");
            return modifyExtension(extension, ExtensionStatus.REJECTED);
        }catch (NotFoundException e){
            return new ResponseEntity(new ErrorMessage("¡La extensión solicitada no existe!"),HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity modifyExtension(ExtensionModel extensionModel, ExtensionStatus extensionStatus ){

        if(!extensionModel.getStatus().equals(ExtensionStatus.PENDING_APPROVAL)) return new ResponseEntity<>(new ErrorMessage("¡Esta prorroga ya fué modificada!"),HttpStatus.BAD_REQUEST);
        extensionModel.setStatus(extensionStatus);
        return ResponseEntity.ok(extensionService.saveExtension(extensionModel));
    }

    private ResponseEntity unauthorizedActionError(){
        return new ResponseEntity(new ErrorMessage("¡Usted no está autorizado a realizar esta acción!"),HttpStatus.UNAUTHORIZED);
    }
}
