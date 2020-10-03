package bibliotecame.back.Extension;

import bibliotecame.back.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/extension")
public class ExtensionController {

    private final ExtensionService extensionService;
    private final UserService userService;

    @Autowired
    public ExtensionController(ExtensionService extensionService, UserService userService) {
        this.extensionService = extensionService;
        this.userService = userService;
    }

    @PostMapping("/{loanId}")
    public ResponseEntity<ExtensionModel> createExtension(@PathVariable int loanId){
        return extensionService.createExtension(loanId);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ExtensionModel> approveExtension(@PathVariable int id){

        if(!userService.findLogged().isAdmin()) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        ExtensionModel extension = extensionService.findById(id);
        return modifyExtension(extension, ExtensionStatus.APPROVED);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ExtensionModel> rejectExtension(@PathVariable int id){

        if(!userService.findLogged().isAdmin()) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        ExtensionModel extension = extensionService.findById(id);
        return modifyExtension(extension, ExtensionStatus.REJECTED);
    }

    public ResponseEntity<ExtensionModel> modifyExtension(ExtensionModel extensionModel, ExtensionStatus extensionStatus ){

        if(!extensionModel.getStatus().equals(ExtensionStatus.PENDING_APPROVAL)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        extensionModel.setStatus(extensionStatus);
        return ResponseEntity.ok(extensionService.saveExtension(extensionModel));
    }
}
