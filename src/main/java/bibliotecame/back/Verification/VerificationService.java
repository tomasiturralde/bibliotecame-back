package bibliotecame.back.Verification;

import bibliotecame.back.Email.Email;
import bibliotecame.back.Email.EmailSender;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Service
@Transactional
public class VerificationService {

    private final UserService userService;
    private final VerificationRepository verificationRepository;

    @Autowired
    public VerificationService(UserService userService, VerificationRepository verificationRepository) {
        this.userService = userService;
        this.verificationRepository = verificationRepository;
    }

    public VerificationModel saveVerification(VerificationModel verificationModel){
        VerificationModel verification = this.verificationRepository.save(verificationModel);
        EmailSender sender = new EmailSender();
        String body = "Hola " + verification.getUserModel().getFirstName() + ",<br>";
        body +=  "<br>Nos alegra que te hayas registrado en <strong>Bibliotecame</strong>, solo hace falta que verifiques tu cuenta para poder acceder a la aplicación. <br><br>" +
                    "Para verificar tu cuenta haz click aquí: http://localhost:3000/verify/"+verification.getToken() +
                    "<br>¡Esperamos que disfrutes nuestro servicio, gracias por elegirnos!";
        body += "<br><br><i>Atentamente, la administración de Bibliotecame.</i>";
        try {
            Email email = new Email(new InternetAddress(verification.getUserModel().getEmail()),"¡Bienvenido a Bibliotecame!",body);
            sender.notifyWithGmail(email);
        } catch (AddressException e) {
        }
        return verification;
    }

    public VerificationModel savePasswordVerification(VerificationModel verificationModel){
        VerificationModel verification = this.verificationRepository.save(verificationModel);
        EmailSender sender = new EmailSender();
        String body = "Hola " + verification.getUserModel().getFirstName() + ",<br>";
        body +=  "<br>Has iniciado el proceso para restaurar tu contraseña.<br><br>" +
                "Para continuar solo tienes que ingresar aquí: http://localhost:3000/reset/"+verification.getToken();
        body += "<br><br><i>Atentamente, la administración de Bibliotecame.</i>";
        try {
            Email email = new Email(new InternetAddress(verification.getUserModel().getEmail()),"Restauración de contraseña",body);
            sender.notifyWithGmail(email);
        } catch (AddressException e) {
        }
        return verification;
    }

    public void deleteVerification(VerificationModel verificationModel){
        verificationRepository.delete(verificationModel);
    }

    public VerificationModel findVerificationByToken(String token){
        return verificationRepository.findByToken(token).orElseThrow(() -> new RuntimeException("¡El token solicitado no es valido!"));
    }

    public VerificationModel findVerificationByUserModel(UserModel userModel){
        return verificationRepository.findByUserModel(userModel).orElseThrow(() -> new RuntimeException("¡El usuario solicitado no necesita ser verificado!"));
    }
}
